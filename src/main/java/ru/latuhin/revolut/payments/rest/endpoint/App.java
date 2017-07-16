package ru.latuhin.revolut.payments.rest.endpoint;

import java.util.Map;
import java.util.NavigableMap;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Account;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;

public class App {

  public static TransactionEndpoint api;

  public static void main(String[] args) {
    api.getTransaction();
    api.postTransaction();
  }

  public void setStorage(NavigableMap<Long, Transaction> map, Map<Long, Account> accountMap) {
    if (api == null) {
      api = new TransactionEndpoint(map, accountMap);
    } else {
      api.transactionStorage = map;
      api.accountStorage = accountMap;
    }
  }
}
