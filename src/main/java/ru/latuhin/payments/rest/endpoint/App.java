package ru.latuhin.payments.rest.endpoint;

import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.payments.rest.endpoint.dao.User;
import spark.Service;

public class App {

  private final Service service;

  public App() {
    service = Service.ignite();
  }
  public App(int port) {
    service = Service.ignite();
    service.port(port);
  }

  public static final YamlTransformer yamlTransformer = new YamlTransformer();
  public static TransactionEndpoint transactionEndpoint;
  public static AccountEndpoint accountEndpoint;
  public static UserEndpoint userEndpoint;

  public void setup() {
    this.declareRoutes();
  }

  private void declareRoutes() {
    service.get("/hearthbeat", (request, response) -> "live");

    service.path("/api/1.0", () -> {
      service.get("/transactions/:id", "application/yaml",
          transactionEndpoint.findById(),
          yamlTransformer);
      service.post("/transactions/from/:from/to/:to/amount/:amount", "text/plain",
          transactionEndpoint::createTransaction, yamlTransformer);

      service.get("/accounts/:id", "application/yaml", accountEndpoint::findAccount, yamlTransformer);
      service.get("/accounts/:id/transactions", "application/yaml", accountEndpoint::getTransactions,
          yamlTransformer);

      service.get("/users/:id", "application/yaml", userEndpoint::findUser, yamlTransformer);
      service.get("/users/:id/accounts", "application/yaml", accountEndpoint::findByUserId,
          yamlTransformer);
      service.get("/users/:id/transactions", "application/yaml", ((request, response) -> {
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
    service.stop();
  }
}
