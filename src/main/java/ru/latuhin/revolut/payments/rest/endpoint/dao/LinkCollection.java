package ru.latuhin.revolut.payments.rest.endpoint.dao;

class LinkCollection {
  public final String rel;
  public final String href;

  public LinkCollection(String type, long userId) {
    rel = "collection/" + type;
    href = "/api/users/" + userId + "/" + type;
  }
}
