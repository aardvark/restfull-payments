package ru.latuhin.revolut.payments.rest.endpoint;

import java.util.Map;
import java.util.NavigableMap;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Account;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;

public class App {

  public static TransactionEndpoint transactionEndpoint;
  public static AccountEndpoint accountEndpoint;
  public static final YamlTransformer yamlTransformer = new YamlTransformer();

  public static void main(String[] args) {
    transactionEndpoint.get();
    transactionEndpoint.post();
    accountEndpoint.get();
  }

  public void setStorage(NavigableMap<Long, Transaction> map, Map<Long, Account> accountMap) {
    if (transactionEndpoint == null) {
      transactionEndpoint = new TransactionEndpoint(map, accountMap, yamlTransformer);
    } else {
      transactionEndpoint.transactionStorage = map;
      transactionEndpoint.accountStorage = accountMap;
    }

    if (accountEndpoint == null) {
      accountEndpoint = new AccountEndpoint(accountMap, yamlTransformer);
    } else {
      accountEndpoint.storage = accountMap;
    }
  }
}
