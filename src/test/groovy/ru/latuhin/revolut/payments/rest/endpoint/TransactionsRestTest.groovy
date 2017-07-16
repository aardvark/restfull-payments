package ru.latuhin.revolut.payments.rest.endpoint

import ru.latuhin.revolut.payments.rest.endpoint.dao.Status
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

class TransactionsRestTest extends Specification {
  String endpoint = 'localhost:4567'
  def transformer = new YamlTransformer()

  def "HTTP GET request for transaction should be present"() {
    given:
    long id = 1
    def storage = new TreeMap()
    storage[id] = new Transaction(1l, 4l, 5l, 22.1, Status.Open)
    def app = new App()
    app.setStorage(storage)
    App.main(null)
    def connection = new URL(
        "http://$endpoint/api/1.0/transaction/$id"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 200
    def s = connection.inputStream.withCloseable { iStream ->
      new BufferedReader(new InputStreamReader(iStream))
          .lines().collect(Collectors.joining("\n"))
    }
    def transaction = transformer.toResource(Transaction.class, s)
    transaction == storage[1l]
  }

  def "HTTP GET request for missing transaction should return 404 code"() {
    given:
    long id = 1
    def storage = new TreeMap()
    def app = new App()
    app.setStorage(storage)
    App.main(null)
    def connection = new URL(
        "http://$endpoint/api/1.0/transaction/$id"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 404
  }

  def "HTTP POST for transaction should add new transaction to the storage"() {
    long id = 1
    long from = 2
    long to = 3
    def amount = 22.2
    def storage = new TreeMap<>()
    def app = new App()
    app.setStorage(storage)
    App.main(null)
    List<HttpURLConnection> conns = []
    2.times {
      def url = new URL(
          "http://$endpoint/api/1.0/transaction/from/$from/to/$to/amount/$amount"
      )
      def connection = url.openConnection() as HttpURLConnection
      connection.setRequestMethod("POST")
      connection.setRequestProperty("Accept", "text/plain")
      conns.add(connection)
    }

    expect:
    conns.collect { it.responseCode } == [200, 200]
    conns.collect {
      it.getHeaderField("Link")
    } == ["/api/1.0/transaction/1", "/api/1.0/transaction/2"]
    storage.size() == 2
  }

  def "HTTP POST transaction should work correctly in parallel"() {
    long from = 2
    long to = 3
    def amount = 10
    def storage = new TreeMap<>()
    def app = new App()
    app.setStorage(storage)
    App.main(null)
    List<HttpURLConnection> conns = []
    1000.times {
      def url = new URL(
          "http://$endpoint/api/1.0/transaction/from/$from/to/$to/amount/$amount"
      )
      def connection = url.openConnection() as HttpURLConnection
      connection.setRequestMethod("POST")
      connection.setRequestProperty("Accept", "text/plain")
      conns.add(connection)
    }
    def codes = conns.parallelStream().map({ it -> it.responseCode }).collect(Collectors.toList())

    expect:
    storage.size() == 1000

  }


}
