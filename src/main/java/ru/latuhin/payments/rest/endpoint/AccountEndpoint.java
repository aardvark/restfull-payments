package ru.latuhin.payments.rest.endpoint;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.eclipse.jetty.http.HttpHeader;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Error;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.payments.rest.endpoint.dao.User;
import ru.latuhin.payments.rest.endpoint.serializers.SerializableResource;
import spark.Request;
import spark.Response;

public class AccountEndpoint {

  NavigableMap<Long, Account> storage;
  YamlTransformer transformer;
  NavigableMap<Long, Transaction> transactionStorage;
  NavigableMap<Long, User> userStorage;
  private Lock writeLock = new ReentrantLock();
  private String createUri = "/api/1.0/accounts/";

  public AccountEndpoint(
      NavigableMap<Long, Account> storage,
      NavigableMap<Long, Transaction> transactionMap,
      NavigableMap<Long, User> userMap,
      YamlTransformer transformer) {
    this.storage = storage;
    this.transactionStorage = transactionMap;
    this.userStorage = userMap;
    this.transformer = transformer;
  }

  public List<Transaction> getTransactions(Request request, Response response) {
    long accountId = getLongParam(request.params(":id"));
    return getTransactionsByAccountId(accountId);
  }

  public List<Transaction> getTransactions(Set<Long> accountIds) {
    return transactionStorage.values().stream()
        .filter(transaction -> transaction.matchAccount(accountIds)).collect(toList());
  }

  public List<Transaction> getTransactionsByAccountId(long accountId) {
    return transactionStorage.values().stream()
        .filter(transaction -> transaction.matchAccount(accountId)).collect(toList());
  }

  public SerializableResource findAccount(Request req, Response res) {
    long id = getLongParam(req.params(":id"));
    Account account = storage.get(id);
    if (account == null) {
      res.status(404);
      return new Error(req.pathInfo(), res.status(), "Account with id " + id + " not found");
    }
    return account;
  }

  public List<Account> findByUserId(Request req, Response res) {
    long id = getLongParam(req.params(":id"));
    return storage.values().stream().filter(account -> account.matchByUser(id)).collect(
        Collectors.toList());
  }

  public SerializableResource createAccount(Request req, Response res) {
    long userId = getLongParam(req.params(":userId"));
    if (userStorage.containsKey(userId)) {
      try {
        writeLock.lock();
        Account account = createAccount(userId);
        res.header(HttpHeader.LOCATION.asString(), createUri(account.id));
        res.status(201);
        return account;
      } finally {
        writeLock.unlock();
      }
    } else {
      res.status(404);
      return new Error(req.pathInfo(), 404, "Can't create account for userId: " + userId + ". User not found");
    }
  }

  private String createUri(long id) {
    return this.createUri + id;
  }

  private Account createAccount(long userId) {
    long id;
    if (storage.isEmpty()) {
      id = 1L;
    } else {
      id = storage.lastKey() + 1;
    }
    Account account = new Account(id, userId);
    storage.put(account.id, account);
    return account;
  }

  private long getLongParam(String params) {
    return Long.parseLong(params);
  }
}
