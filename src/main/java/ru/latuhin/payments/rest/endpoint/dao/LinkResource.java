package ru.latuhin.payments.rest.endpoint.dao;

import java.util.Objects;

class LinkResource {
  public final long id;
  public final String rel;
  public final String href;

  LinkResource(String type, long id) {
    this.id = id;
    this.rel = "resource/" + type;
    this.href = "/api/" + type + "/" + id;
  }

  @Override
  public String toString() {
    return "LinkResource{" +
        "id=" + id +
        ", rel='" + rel + '\'' +
        ", href='" + href + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LinkResource that = (LinkResource) o;
    return id == that.id &&
        Objects.equals(rel, that.rel) &&
        Objects.equals(href, that.href);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, rel, href);
  }
}
