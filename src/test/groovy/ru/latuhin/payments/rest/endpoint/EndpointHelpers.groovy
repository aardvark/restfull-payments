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

  static void setupApi(NavigableMap transactionStorage, Map accountStorage) {
    def app = new App()
    app.setStorage(transactionStorage, accountStorage)
    app.setup()
  }
}
