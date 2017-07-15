package ru.latuhin.revolut.payments.rest.endpoint;

public class ErrorMessage {
  long code;
  String message;

  public ErrorMessage(long code, String message) {
    this.code = code;
    this.message = message;
  }
}
