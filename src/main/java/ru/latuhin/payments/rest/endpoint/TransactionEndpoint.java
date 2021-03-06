package ru.latuhin.payments.rest.endpoint;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.jetty.http.HttpHeader;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Error;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.payments.rest.endpoint.serializers.SerializableResource;
import spark.Request;
import spark.Response;
import spark.Route;

public class TransactionEndpoint {

  Map<Long, Account> accountStorage;
  NavigableMap<Long, Transaction> transactionStorage;
  private Lock transactionWrite = new ReentrantLock();
  private Lock accountWrite = new ReentrantLock();
  private YamlTransformer transformer = new YamlTransformer();

  public TransactionEndpoint(
      NavigableMap<Long, Transaction> transactionStorage,
      Map<Long, Account> accountStorage,
      YamlTransformer transformer) {
    this.transformer = transformer;
    this.transactionStorage = transactionStorage;
    this.accountStorage = accountStorage;
  }

  public Route findById() {
    return (req, res) -> {
      long id = getLongParam(req.params(":id"));
      Transaction transaction = transactionStorage.get(id);
      if (transaction == null) {
        res.status(404);
        return new Error(req.pathInfo(), 404, "Transaction with id " + id + " not found");
      }
      return transaction;
    };
  }

  public SerializableResource createTransaction(Request request, Response response) {
    long from = getLongParam(request.params(":from"));

    if (!accountStorage.containsKey(from)) {
      response.status(404);
      return new Error(request.pathInfo(), 404, "Account with id " + from + " not found");
    }

    long to = getLongParam(request.params(":to"));
    if (!accountStorage.containsKey(to)) {
      response.status(404);
      return new Error(request.pathInfo(), 404, "Account with id " + to + " not found");
    }

    BigDecimal amount = new BigDecimal(request.params(":amount"));

    Transaction transaction;
    try {
      accountWrite.lock();
      Account fromAccount = accountStorage.get(from);
      BigDecimal checkedAmount = fromAccount.amount;
      if (checkedAmount.compareTo(amount) < 0) {
        response.status(424);
        return new Error(request.pathInfo(), 424,
            "Account with id " + from + " balance to low [need=" + amount + "; have="
                + checkedAmount + "]");
      }
      try {
        transactionWrite.lock();
        transaction = createTransaction(from, to, amount);
        accountStorage.compute(from, (aLong, account) -> new Account(account, amount));
        accountStorage.compute(to, (aLong, account) -> new Account(account, amount.negate()));
      } finally {
        transactionWrite.unlock();
      }
    } finally {
      accountWrite.unlock();
    }

    response.header(HttpHeader.LOCATION.toString(), "/api/1.0/transaction/" + transaction.id);
    return transactionStorage.get(transaction.id);
  }

  private Transaction createTransaction(long from, long to, BigDecimal amount) {
    long id;
    if (transactionStorage.isEmpty()) {
      id = 1L;
    } else {
      id = transactionStorage.lastKey() + 1;
    }
    Transaction transaction = new Transaction(id, from, to, amount);
    transactionStorage.put(transaction.id, transaction);
    return transaction;
  }

  private long getLongParam(String params) {
    return Long.parseLong(params);
  }
}
