package ru.latuhin.payments.rest.endpoint;

import java.util.Map;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import spark.Spark;

public class AccountEndpoint {
  Map<Long, Account> storage;
  YamlTransformer transformer;
  String pathPrefix = "/api/1.0/accounts/";

  public AccountEndpoint(
      Map<Long, Account> storage,
      YamlTransformer transformer) {
    this.storage = storage;
    this.transformer = transformer;
  }

  public void get() {
    Spark.get(pathPrefix + ":id",
        (req, res) -> {
          long id = getLongParam(req.params(":id"));
          return storage.get(id);
        },
        new YamlTransformer());
  }

  private long getLongParam(String params) {
    return Long.parseLong(params);
  }

}
