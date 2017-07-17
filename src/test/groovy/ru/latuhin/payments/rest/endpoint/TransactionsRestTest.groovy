package ru.latuhin.payments.rest.endpoint

import ru.latuhin.payments.rest.endpoint.dao.Account
import ru.latuhin.payments.rest.endpoint.dao.Error
import ru.latuhin.payments.rest.endpoint.dao.Status
import ru.latuhin.payments.rest.endpoint.dao.Transaction
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

class TransactionsRestTest extends Specification {
  String endpoint = 'localhost:4567'
  def transformer = new YamlTransformer()

  def "GET request should resolve"() {
    given:
    long id = 1
    def storage = new TreeMap()
    storage[id] = new Transaction(1l, 4l, 5l, 22.1, Status.Open)
    def app = new App()
    app.setStorage(storage, [:])
    App.main(null)
    def connection = new URL(
        "http://$endpoint/api/1.0/transaction/$id"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 200
    def body = connection.inputStream.withCloseable { iStream ->
      new BufferedReader(new InputStreamReader(iStream))
          .lines().collect(Collectors.joining("\n"))
    }
    def transaction = transformer.toResource(Transaction.class, body)
    transaction == storage[1l]
  }

  def "GET request for missing should return 404 code"() {
    given:
    long id = 1
    def storage = new TreeMap()
    def app = new App()
    app.setStorage(storage, [:])
    App.main(null)

    def connection = new URL(
        "http://$endpoint/api/1.0/transaction/$id"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 404
    def body = connection.errorStream.withCloseable { iStream ->
      new BufferedReader(new InputStreamReader(iStream))
          .lines().collect(Collectors.joining("\n"))
    }
    def error = transformer.toResource(Error.class, body)
    error.errorCode == 404
    error.request == "/api/1.0/transaction/$id"
    error.message == "Transaction with id $id not found"
  }

  def "POST for transaction should add new transaction to the storage"() {
    long from = 2
    long to = 3
    def amount = 22.2
    def storage = new TreeMap<>()
    def app = new App()
    def accounts = [(from): new Account(from, 0, new BigDecimal(100)), (to): new Account(to, 0)]
    app.setStorage(storage, accounts)
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

  def "POST transaction should work correctly in parallel"() {
    long from = 2
    long to = 3
    def numberOfTransaction = 100
    def amount = 10
    def storage = new TreeMap<>()
    def app = new App()
    def accounts = [(from): new Account(from, 0, new BigDecimal(numberOfTransaction * amount)), (to):
        new Account(to, 0)]
    app.setStorage(storage, accounts)
    App.main(null)
    List<HttpURLConnection> conns = []
    numberOfTransaction.times {
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
    storage.size() == numberOfTransaction
    accounts[from].amount == BigDecimal.ZERO
  }

  @Unroll
  def "POST create should fail if #account account is missing"() {
    given:
    def amount = 22.2
    def storage = new TreeMap<>()
    def app = new App()
    app.setStorage(storage, accounts)
    App.main(null)

    when:
    def url = new URL(
        "http://$endpoint/api/1.0/transaction/from/$from/to/$to/amount/$amount"
    )
    def connection = url.openConnection() as HttpURLConnection
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Accept", "text/plain")

    then:
    connection.responseCode == 404

    where:
    from | to | accounts
    -1   | -1 | [:]
    1    | -1 | [(from): new Account(from, 0)]

    account = from == -1 ? "from" : "to"
  }

  def "post should fail if amount on account is not enough"() {
    long from = 1
    long to = 2
    def amount = 22.2
    def storage = new TreeMap<>()
    def app = new App()
    def accounts = [(from): new Account(from, 0), (to): new Account(to, 0)]
    app.setStorage(storage, accounts)
    App.main(null)
    def url = new URL(
        "http://$endpoint/api/1.0/transaction/from/$from/to/$to/amount/$amount"
    )
    def connection = url.openConnection() as HttpURLConnection
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Accept", "text/plain")

    expect:
    connection.responseCode == 424
  }

  def "post should deduce from account value by transaction amount"() {
    long from = 1
    long to = 2
    def amount = 42.0
    def storage = new TreeMap<>()
    def app = new App()
    def fromStaringAmount = new BigDecimal(100)
    def accounts = [(from): new Account(from, 0, fromStaringAmount), (to): new Account(to, 0)]
    app.setStorage(storage, accounts)
    App.main(null)
    def url = new URL(
        "http://$endpoint/api/1.0/transaction/from/$from/to/$to/amount/$amount"
    )
    def connection = url.openConnection() as HttpURLConnection
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Accept", "text/plain")

    expect:
    connection.responseCode == 200
    accounts[from].amount == fromStaringAmount.minus(amount)
  }


}
