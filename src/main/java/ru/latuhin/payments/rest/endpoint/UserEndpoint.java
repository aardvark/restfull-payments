package ru.latuhin.payments.rest.endpoint;

import static java.util.Optional.ofNullable;

import java.util.Map;
import ru.latuhin.payments.rest.endpoint.dao.Error;
import ru.latuhin.payments.rest.endpoint.dao.User;
import ru.latuhin.payments.rest.endpoint.serializers.SerializableResource;
import spark.Request;
import spark.Response;

public class UserEndpoint {

  public Map<Long, User> storage;

  public UserEndpoint(Map<Long, User> userStorage) {
    this.storage = userStorage;
  }

  public SerializableResource findUser(Request request, Response response) {
    long id = getLongParam(request.params(":id"));
    SerializableResource user = storage.get(id);
    return ofNullable(user).orElseGet(() -> {
      response.status(404);
      return new Error(request.pathInfo(), response.status(), "User with id " + id + " not found");
    });
  }

  private long getLongParam(String params) {
    return Long.parseLong(params);
  }
}
