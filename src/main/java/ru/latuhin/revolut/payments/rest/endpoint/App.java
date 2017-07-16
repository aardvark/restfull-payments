package ru.latuhin.revolut.payments.rest.endpoint;

import java.util.NavigableMap;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;

public class App {

  public static Api api;

  public static void main(String[] args) {
    api.getTransaction();
    api.postTransaction();
  }

  public void setStorage(NavigableMap<Long, Transaction> map) {
    if (api == null) {
      api = new Api(map);
    } else {
      api.transactionStorage = map;
    }
  }
}
