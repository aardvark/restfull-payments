package ru.latuhin.payments.rest.endpoint

import java.util.stream.Collectors

class EndpointHelpers {
  static String grabBody(HttpURLConnection connection) {
    def stream
    if ([404, 424].contains(connection.responseCode)) {
      stream = connection.errorStream
    } else {
      stream = connection.inputStream
    }

    stream.withCloseable { iStream ->
      new BufferedReader(new InputStreamReader(iStream))
          .lines().collect(Collectors.joining("\n"))
    }
  }

  static App setupApi(NavigableMap transactionStorage, Map accountStorage, NavigableMap userStorage) {
    def app = new App()
    app.setStorage(transactionStorage, accountStorage, userStorage)
    app.setup()
    return app
  }

  static App setupApi(Map transactionStorage, Map accountStorage, Map userStorage) {
    def app = new App()
    app.setStorage(new TreeMap(transactionStorage), accountStorage, new TreeMap(userStorage))
    app.setup()
    return app
  }

  public static HttpURLConnection get(String url) {
    new URL(url).openConnection() as HttpURLConnection
  }
}
