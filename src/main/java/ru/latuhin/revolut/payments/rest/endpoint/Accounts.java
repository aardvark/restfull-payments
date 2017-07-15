package ru.latuhin.revolut.payments.rest.endpoint;

import java.math.BigDecimal;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Account;
import ru.latuhin.revolut.payments.rest.endpoint.dao.storage.Storage;

public class Accounts {

  public static final int ACCOUNT_NOT_EXIST = -1;
  static Storage storage;

  public static BigDecimal ensureAmount(long from, String transferAmount) {
    Account account = null;
    BigDecimal amount = new BigDecimal(transferAmount);
    if (account.amount.compareTo(amount) > 0) {
      return amount;
    } else {
      return BigDecimal.ZERO;
    }
  }

  public static long ensureExists(String accountId) {
    long id = new Long(accountId);
    if (storage.exist(id)) {
      return id;
    } else {
      return ACCOUNT_NOT_EXIST;
    }
  }
}
