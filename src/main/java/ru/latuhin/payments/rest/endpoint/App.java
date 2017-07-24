package ru.latuhin.payments.rest.endpoint;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

import java.util.Map;
import java.util.NavigableMap;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.payments.rest.endpoint.dao.User;

public class App {

  public static final YamlTransformer yamlTransformer = new YamlTransformer();
  public static TransactionEndpoint transactionEndpoint;
  public static AccountEndpoint accountEndpoint;

  public void setup() {
    this.declareRoutes();
  }

  private void declareRoutes() {
    path("/api/1.0", () -> {
      get("/transactions/:id", "application/yaml",
          transactionEndpoint.findById(),
          yamlTransformer);
      post("/transactions/from/:from/to/:to/amount/:amount", "text/plain",
          transactionEndpoint::createTransaction, yamlTransformer);

      get("/accounts/:id", "application/yaml", accountEndpoint::findAccount, yamlTransformer);
      get("/accounts/:id/transactions", "application/yaml", accountEndpoint::getTransactions,
          yamlTransformer);

      get("/users/:id", "application/yaml", (request, response) -> new User(0), yamlTransformer);
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
