package ru.latuhin.revolut.payments.rest.endpoint.dao;

import java.util.Objects;

public class LinkSubCollection {
  public final String rel;
  public final String href;

  public LinkSubCollection(String selfType, long selfId, String subCollectionType) {
    rel = "collection/" + subCollectionType;
    href = "/api/" + selfType + "/" + selfId + "/" + subCollectionType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LinkSubCollection that = (LinkSubCollection) o;
    return Objects.equals(rel, that.rel) &&
        Objects.equals(href, that.href);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rel, href);
  }
}
