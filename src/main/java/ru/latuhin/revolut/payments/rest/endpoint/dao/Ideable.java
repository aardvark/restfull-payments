package ru.latuhin.revolut.payments.rest.endpoint.dao;

public interface Ideable<T> {
  T getId();
  T forEmpty();
  T next(T previous);

}
