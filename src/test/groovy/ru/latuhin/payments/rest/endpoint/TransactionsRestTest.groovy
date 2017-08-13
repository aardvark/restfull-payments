package ru.latuhin.payments.rest.endpoint

import org.eclipse.jetty.http.HttpHeader
import ru.latuhin.payments.rest.endpoint.dao.Account
import ru.latuhin.payments.rest.endpoint.dao.Error
import ru.latuhin.payments.rest.endpoint.dao.Status
import ru.latuhin.payments.rest.endpoint.dao.Transaction
import spock.lang.Specification
import spock.lang.Unroll

import static ru.latuhin.payments.rest.endpoint.EndpointHelpers.setupApi

class TransactionsRestTest extends Specification {
  String endpoint = 'localhost:4567'
  def transformer = new YamlTransformer()

  def "GET request should resolve"() {
    given:
    long id = 1
    def storage = new TreeMap()
    storage[id] = new Transaction(1l, 4l, 5l, 22.1, Status.Open)
    setupApi(storage, new TreeMap(), new TreeMap())

    def connection = new URL(
        "http://$endpoint/api/1.0/transactions/$id"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 200
    def body = EndpointHelpers.grabBody(connection)
    def transaction = transformer.toResource(Transaction.class, body)
    transaction[0] == storage[1l]
  }


  def "GET request for missing should return 404 code"() {
    given:
    long id = 1
    def storage = new TreeMap()
    setupApi(storage, new TreeMap(), new TreeMap())

    def connection = new URL(
        "http://$endpoint/api/1.0/transactions/$id"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 404
    transformer.toResource(Error.class, EndpointHelpers.grabBody(connection))[0] == new Error(
        request: "/api/1.0/transactions/$id",
        errorCode: connection.responseCode,
        message: "Transaction with id $id not found"
    )
  }

  def "POST for transaction should add new transaction to the storage"() {
    long from = 2
    long to = 3
    def amount = 22.2
    def storage = new TreeMap<>()
    def accounts = [(from): new Account(from, 0, new BigDecimal(100)), (to): new Account(to, 0)] as TreeMap
    setupApi(storage, accounts, new TreeMap())
    List<HttpURLConnection> conns = []
    2.times {
      def url = new URL(
          "http://$endpoint/api/1.0/transactions/from/$from/to/$to/amount/$amount"
      )
      def connection = url.openConnection() as HttpURLConnection
      connection.setRequestMethod("POST")
      connection.setRequestProperty("Accept", "application/yaml")
      conns.add(connection)
    }

    expect:
    conns.collect { it.responseCode } == [200, 200]
    conns.collect {
      it.getHeaderField(HttpHeader.LOCATION.toString())
    } == ["/api/1.0/transaction/1", "/api/1.0/transaction/2"]
    storage.size() == 2
    accounts[from].amount == 100 - (22.2 * 2)
    accounts[to].amount == (22.2 * 2)
  }

  def "POST transaction should work correctly in parallel"() {
    long from = 2
    long to = 3
    def numberOfTransaction = 100
    def amount = 10
    def storage = new TreeMap<>()
    def accounts = [(from): new Account(from, 0, new BigDecimal(numberOfTransaction * amount)),
                    (to)  : new Account(to, 0)] as TreeMap
    setupApi(storage, accounts, new TreeMap())
    EndpointHelpers.runParallel(numberOfTransaction,
        "http://$endpoint/api/1.0/transactions/from/$from/to/$to/amount/$amount") { it -> it.responseCode }

    expect:
    storage.size() == numberOfTransaction
    accounts[from].amount == BigDecimal.ZERO
  }


  @Unroll
  def "POST create should fail if #account account is missing"() {
    given:
    def amount = 22.2
    def storage = new TreeMap<>()
    setupApi(storage, accounts, new TreeMap())

    when:
    def url = new URL(
        "http://$endpoint/api/1.0/transactions/from/$from/to/$to/amount/$amount"
    )
    def connection = url.openConnection() as HttpURLConnection
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Accept", "application/yaml")

    then:
    connection.responseCode == 404

    where:
    from | to  | accounts
    -1l  | -1l | [:] as TreeMap
    1l   | -1l | [(from): new Account(from, 0)] as TreeMap

    account = from == -1 ? "from" : "to"
  }

  def "post should fail if amount on account is not enough"() {
    long from = 1
    long to = 2
    def amount = 22.2
    def storage = new TreeMap<>()
    def accounts = [(from): new Account(from, 0), (to): new Account(to, 0)] as TreeMap
    setupApi(storage, accounts, new TreeMap())
    def url = new URL(
        "http://$endpoint/api/1.0/transactions/from/$from/to/$to/amount/$amount"
    )
    def connection = url.openConnection() as HttpURLConnection
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Accept", "application/yaml")

    expect:
    connection.responseCode == 424

    def bodyError = transformer.toResource(Error.class, EndpointHelpers.grabBody(connection))
    with(bodyError[0]) {
      request == "/api/1.0/transactions/from/$from/to/$to/amount/$amount"
      errorCode == connection.responseCode
      message == "Account with id $from balance to low [need=$amount; have=0]"

    }
  }

  def "post should deduce from account value by transaction amount"() {
    long from = 1
    long to = 2
    def amount = 42.0
    def storage = new TreeMap<>()
    def fromStaringAmount = new BigDecimal(100)
    def accounts = [(from): new Account(from, 0, fromStaringAmount), (to): new Account(to, 0)] as
        TreeMap
    setupApi(storage, accounts, new TreeMap())
    def url = new URL(
        "http://$endpoint/api/1.0/transactions/from/$from/to/$to/amount/$amount"
    )
    def connection = url.openConnection() as HttpURLConnection
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Accept", "application/yaml")

    expect:
    connection.responseCode == 200
    accounts[from].amount == fromStaringAmount.minus(amount)
  }


}
