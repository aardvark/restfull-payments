package ru.latuhin.payments.rest.endpoint;

import java.util.HashMap;
import java.util.TreeMap;

public class Launcher {
  public static Bootstrap bootstrap = new Bootstrap();

  public static void main(String[] args) {
    String property = System.getProperty("api.bind.port");
    App app;
    if (property != null) {
      int port = Integer.parseInt(property);
      app = new App(port);
    } else {
      app = new App();
    }

    if (args != null && args[0] != null && "bootstrap".equals(args[0])) {
      app.setStorage(new TreeMap<>(), bootstrap.createAccounts(), bootstrap.createUsers());
    } else {
      app.setStorage(new TreeMap<>(), new HashMap<>(), new TreeMap<>());
    }
    app.setup();
  }
}
