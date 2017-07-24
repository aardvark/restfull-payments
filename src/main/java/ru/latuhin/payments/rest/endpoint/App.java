package ru.latuhin.payments.rest.endpoint;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

import java.util.Map;
import java.util.NavigableMap;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;

public class App {

  public static final YamlTransformer yamlTransformer = new YamlTransformer();
  public static TransactionEndpoint transactionEndpoint;
  public static AccountEndpoint accountEndpoint;

  public static void main(String[] args) {
    path("/api/1.0", () -> {
      get("/transactions/:id", "application/yaml",
          transactionEndpoint.findById(),
          yamlTransformer);
      post("/transactions/from/:from/to/:to/amount/:amount", "text/plain",
          transactionEndpoint.createTransaction(), yamlTransformer);

      get("/accounts/:id", accountEndpoint::findAccount,
          accountEndpoint.transformer);
      get("/accounts/:id/transactions", accountEndpoint::getTransactions,
          accountEndpoint.transformer);
    });
  }

  public void setStorage(NavigableMap<Long, Transaction> transactionMap,
      Map<Long, Account> accountMap) {
    if (transactionEndpoint == null) {
      transactionEndpoint = new TransactionEndpoint(transactionMap, accountMap, yamlTransformer);
    } else {
      transactionEndpoint.transactionStorage = transactionMap;
      transactionEndpoint.accountStorage = accountMap;
    }

    if (accountEndpoint == null) {
      accountEndpoint = new AccountEndpoint(accountMap, transactionMap, yamlTransformer);
    } else {
      accountEndpoint.storage = accountMap;
      accountEndpoint.transactionStorage = transactionMap;
    }
  }
}
