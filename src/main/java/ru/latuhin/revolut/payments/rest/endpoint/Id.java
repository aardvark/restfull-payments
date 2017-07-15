package ru.latuhin.revolut.payments.rest.endpoint;

interface Id {
  <I extends Id> I next();
}
