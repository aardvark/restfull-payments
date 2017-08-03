package ru.latuhin.payments.rest.endpoint;

import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.jetty.http.HttpHeader;
import ru.latuhin.payments.rest.endpoint.dao.Error;
import ru.latuhin.payments.rest.endpoint.dao.User;
import ru.latuhin.payments.rest.endpoint.serializers.SerializableResource;
import spark.Request;
import spark.Response;

public class UserEndpoint {

  public NavigableMap<Long, User> storage;
  private Lock usersWrite = new ReentrantLock();

  public UserEndpoint(NavigableMap<Long, User> userStorage) {
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

  public SerializableResource addUser(Request request, Response response) {
    User user;
    try {
      usersWrite.lock();
      user = createUser();
    } finally {
      usersWrite.unlock();
    }
    response.status(201);
    response.header(HttpHeader.LOCATION.asString(), request.scheme() + "://" + request.host() + "/api/1.0/users/" + user.id);
    return user;
  }

  private User createUser() {
    long id;
    if (storage.isEmpty()) {
      id = 1L;
    } else {
      id = storage.lastKey() + 1;
    }
    User user;
    user = new User(id);
    storage.put(user.id, user);
    return user;
  }
}
