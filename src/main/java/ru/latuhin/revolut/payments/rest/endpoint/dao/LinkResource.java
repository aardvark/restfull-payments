package ru.latuhin.revolut.payments.rest.endpoint.dao;

class LinkResource {
  public final long id;
  public final String rel;
  public final String href;

  LinkResource(String type, long id) {
    this.id = id;
    this.rel = "resource/" + type;
    this.href = "/api/" + type + "/" + id;
  }
}
