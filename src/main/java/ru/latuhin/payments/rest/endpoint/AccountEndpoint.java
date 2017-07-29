package ru.latuhin.payments.rest.endpoint;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.stream.Collectors;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Error;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.payments.rest.endpoint.serializers.SerializableResource;
import spark.Request;
import spark.Response;

public class AccountEndpoint {

  Map<Long, Account> storage;
  YamlTransformer transformer;
  NavigableMap<Long, Transaction> transactionStorage;

  public AccountEndpoint(
      Map<Long, Account> storage,
      NavigableMap<Long, Transaction> transactionMap,
      YamlTransformer transformer) {
    this.storage = storage;
    this.transactionStorage = transactionMap;
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

  private long getLongParam(String params) {
    return Long.parseLong(params);
  }
}
