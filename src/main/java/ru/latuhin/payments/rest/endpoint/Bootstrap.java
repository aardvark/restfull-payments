package ru.latuhin.payments.rest.endpoint;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.User;

public class Bootstrap {

  Map<Long, User> createUsers() {
    Map<Long, User> users = new HashMap<>();
    for (long i = 0; i < 10; i++) {
      users.put(i, new User(i));
    }
    return users;
  }

  Map<Long, Account> createAccounts() {
    Map<Long, Account> accounts = new HashMap<>();
    long j = 0;
    for (int i = 0; i < 3; i++) {
      for (long k = 0; k < 10; k++) {
        accounts.put(j, new Account(j++, k, new BigDecimal(1000)));
      }
    }
    return accounts;
  }

}
