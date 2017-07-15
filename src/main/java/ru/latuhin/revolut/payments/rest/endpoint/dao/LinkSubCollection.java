package ru.latuhin.revolut.payments.rest.endpoint.dao;

public class LinkSubCollection {
  public final String rel;
  public final String href;

  public LinkSubCollection(String selfType, long selfId, String subCollectionType) {
    rel = "collection/" + subCollectionType;
    href = "/api/" + selfType + "/" + selfId + "/" + subCollectionType;
  }

}
