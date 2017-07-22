package ru.latuhin.payments.rest.endpoint;

import java.util.Map;
import java.util.NavigableMap;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;

public class App {

  public static TransactionEndpoint transactionEndpoint;
  public static AccountEndpoint accountEndpoint;
  public static final YamlTransformer yamlTransformer = new YamlTransformer();

  public static void main(String[] args) {
    transactionEndpoint.get();
    transactionEndpoint.post();
    accountEndpoint.get();
  }

  public void setStorage(NavigableMap<Long, Transaction> transactionMap, Map<Long, Account> accountMap) {
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
