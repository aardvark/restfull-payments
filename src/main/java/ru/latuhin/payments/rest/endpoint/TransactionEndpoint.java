package ru.latuhin.payments.rest.endpoint;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Error;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;
import spark.Spark;

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

  public void get() {
    transformer = new YamlTransformer();
    Spark.get("/api/1.0/transaction/:id", "application/yaml",
        (req, res) -> {
          long id = getLongParam(req.params(":id"));
          Transaction transaction = transactionStorage.get(id);
          if (transaction == null) {
            res.status(404);
            return new Error(req.pathInfo(), 404, "Transaction with id " + id + " not found");
          }
          return transaction;
        },
        transformer);
  }

  public void post() {
    Spark.post("/api/1.0/transaction/from/:from/to/:to/amount/:amount", "text/plain",
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
