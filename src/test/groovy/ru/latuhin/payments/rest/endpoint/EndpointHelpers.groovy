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

  static App setupApi(NavigableMap transactionStorage, NavigableMap accountStorage, NavigableMap
      userStorage) {
    def app = new App()
    app.setStorage(transactionStorage, accountStorage, userStorage)
    app.setup()
    return app
  }

  public static HttpURLConnection get(String url) {
    new URL(url).openConnection() as HttpURLConnection
  }

  static <T> List<T> runParallel(int numberOfTransaction, String urlString, Closure<T>
      responseTransformer) {
   List<HttpURLConnection> conns = []
   numberOfTransaction.times {
     def url = new URL(
         urlString
     )
     def connection = url.openConnection() as HttpURLConnection
     connection.setRequestMethod("POST")
     connection.setRequestProperty("Accept", "application/yaml")
     conns.add(connection)
   }
   conns.parallelStream().map(responseTransformer).collect(Collectors.toList())
 }
}
