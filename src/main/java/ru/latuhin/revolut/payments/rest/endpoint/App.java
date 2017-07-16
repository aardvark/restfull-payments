package ru.latuhin.revolut.payments.rest.endpoint;

import java.util.Map;
import java.util.NavigableMap;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Account;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;

public class App {

  public static TransactionEndpoint transactionEndpoint;

  public static void main(String[] args) {
    transactionEndpoint.getTransaction();
    transactionEndpoint.postTransaction();
  }

  public void setStorage(NavigableMap<Long, Transaction> map, Map<Long, Account> accountMap) {
    if (transactionEndpoint == null) {
      transactionEndpoint = new TransactionEndpoint(map, accountMap);
    } else {
      transactionEndpoint.transactionStorage = map;
      transactionEndpoint.accountStorage = accountMap;
    }
  }
}
