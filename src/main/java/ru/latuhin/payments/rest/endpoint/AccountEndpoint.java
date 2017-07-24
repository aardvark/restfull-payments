package ru.latuhin.payments.rest.endpoint;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
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

  private long getLongParam(String params) {
    return Long.parseLong(params);
  }

}
