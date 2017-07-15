package ru.latuhin.revolut.payments.rest.endpoint.dao.storage;

public interface Storage<T, Id> {

  T save(T object);

  T findById(Id id);

  Id reserveId();

  boolean exist(Id id);
}
