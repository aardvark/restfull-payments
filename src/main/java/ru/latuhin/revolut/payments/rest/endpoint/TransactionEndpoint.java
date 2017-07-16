package ru.latuhin.revolut.payments.rest.endpoint;

import static spark.Spark.get;
import static spark.Spark.post;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Account;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;

public class TransactionEndpoint {

  Map<Long, Account> accountStorage;
  NavigableMap<Long, Transaction> transactionStorage;
  private Lock transactionWrite = new ReentrantLock();
  private Lock accountWrite = new ReentrantLock();

  public TransactionEndpoint(
      NavigableMap<Long, Transaction> transactionStorage,
      Map<Long, Account> accountStorage) {
    this.transactionStorage = transactionStorage;
    this.accountStorage = accountStorage;
  }

  public void getTransaction() {
    get("/api/1.0/transaction/:id",
        (req, res) -> {
          long id = getLongParam(req.params(":id"));
          return transactionStorage.get(id);
        },
        new YamlTransformer());
  }

  public void postTransaction() {
    post("/api/1.0/transaction/from/:from/to/:to/amount/:amount", "text/plain",
        (request, response) -> {
          long from = getLongParam(request.params(":from"));

          if (!accountStorage.containsKey(from)) {
            response.status(404);
            return response;
          }

          long to = getLongParam(request.params(":to"));
          if (!accountStorage.containsKey(to)) {
            response.status(404);
            return response;
          }

          BigDecimal amount = new BigDecimal(request.params(":amount"));

          Account fromAccount = accountStorage.get(from);
          if (fromAccount.amount.compareTo(amount) < 0) {
            response.status(424);
            return response;
          }

          Transaction transaction;
          try {
            accountWrite.lock();
            try {
              transactionWrite.lock();
              transaction = createTransaction(from, to, amount);
            } finally {
              transactionWrite.unlock();
            }
            accountStorage.compute(from, (aLong, account) -> new Account(account, amount));
          } finally {
            accountWrite.unlock();
          }

          response.header("Link", "/api/1.0/transaction/" + transaction.id);
          return response;

        });
  }

  private Transaction createTransaction(long from, long to, BigDecimal amount) {
    long id;
    if (transactionStorage.isEmpty()) {
      id = 1L;
    } else {
      id = transactionStorage.lastKey() + 1;
    }
    Transaction transaction;
    transaction = new Transaction(id, from, to, amount);
    transactionStorage.put(transaction.id, transaction);
    return transaction;
  }

  private long getLongParam(String params) {
    return Long.parseLong(params);
  }

}
