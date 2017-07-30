package ru.latuhin.payments.rest.endpoint;

import static java.util.stream.Collectors.toSet;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.payments.rest.endpoint.dao.User;
import spark.Spark;

public class App {
  public static final YamlTransformer yamlTransformer = new YamlTransformer();
  public static TransactionEndpoint transactionEndpoint;
  public static AccountEndpoint accountEndpoint;
  public static UserEndpoint userEndpoint;

  public App() {}
  public App(int port) {
    Spark.port(port);
  }

  public void setup() {
    this.declareRoutes();
  }

  private void declareRoutes() {
    get("/hearthbeat", (request, response) -> "live");

    path("/api/1.0", () -> {
      get("/transactions/:id", "application/yaml",
          transactionEndpoint.findById(),
          yamlTransformer);
      post("/transactions/from/:from/to/:to/amount/:amount", "text/plain",
          transactionEndpoint::createTransaction, yamlTransformer);

      get("/accounts/:id", "application/yaml", accountEndpoint::findAccount, yamlTransformer);
      get("/accounts/:id/transactions", "application/yaml", accountEndpoint::getTransactions,
          yamlTransformer);

      get("/users/:id", "application/yaml", userEndpoint::findUser, yamlTransformer);
      get("/users/:id/accounts", "application/yaml", accountEndpoint::findByUserId,
          yamlTransformer);
      get("/users/:id/transactions", "application/yaml", ((request, response) -> {
            List<Account> accounts = accountEndpoint.findByUserId(request, response);
            return accountEndpoint.getTransactions(
                accounts.stream().map(account -> account.id).collect(toSet()));
          }),
          yamlTransformer);
    });
  }

  public void setStorage(NavigableMap<Long, Transaction> transactionStorage,
      Map<Long, Account> accountStorage, Map<Long, User> userStorage) {
    if (transactionEndpoint == null) {
      transactionEndpoint = new TransactionEndpoint(transactionStorage, accountStorage, yamlTransformer);
    } else {
      transactionEndpoint.transactionStorage = transactionStorage;
      transactionEndpoint.accountStorage = accountStorage;
    }

    if (accountEndpoint == null) {
      accountEndpoint = new AccountEndpoint(accountStorage, transactionStorage, yamlTransformer);
    } else {
      accountEndpoint.storage = accountStorage;
      accountEndpoint.transactionStorage = transactionStorage;
    }

    if (userEndpoint == null) {
      userEndpoint = new UserEndpoint(userStorage);
    } else {
      userEndpoint.storage = userStorage;
    }
  }

  public void stop() {
    Spark.stop();
  }
}
