package ru.latuhin.revolut.payments.rest.endpoint.dao.storage;

import java.math.BigDecimal;
import java.util.Optional;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Status;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;

public class TransactionStorage implements Storage<Transaction, Long> {

  @Override
  public Transaction findById(Long id) {
    return new Transaction(id, id, id, new BigDecimal(10.5), Status.Open);
  }

  @Override
  public Transaction save(Transaction object) {
    return null;

  }

  @Override
  public Long reserveId() {
    return null;
  }

  @Override
  public boolean exist(Long aLong) {
    return false;
  }
}
