package ru.latuhin.payments.rest.endpoint

import org.eclipse.jetty.http.HttpHeader
import ru.latuhin.payments.rest.endpoint.dao.Account
import ru.latuhin.payments.rest.endpoint.dao.Error
import ru.latuhin.payments.rest.endpoint.dao.Transaction
import ru.latuhin.payments.rest.endpoint.dao.User
import spock.lang.Shared
import spock.lang.Specification

import static ru.latuhin.payments.rest.endpoint.EndpointHelpers.setupApi


class AccountsRestTest extends Specification {
  String endpoint = 'localhost:4567'
  YamlTransformer transformer = new YamlTransformer()
  @Shared
  def app

  def setupSpec() {
    app = new App()
    app.setStorage(new TreeMap(), new TreeMap<Long, Account>(), new TreeMap<Long, User>())
    app.setup()
  }

  def cleanupSpec() {
    app.stop()
  }

  def "GET request should resolve"() {
    given:
    long id = 1
    long userId = 1
    def accounts = new TreeMap()
    accounts[id] = new Account(id, userId)
    setupApi(new TreeMap(), accounts, new TreeMap())
    def connection = new URL(
        "http://$endpoint/api/1.0/accounts/$id"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 200
    def accountAnswer = transformer.toResource(Account.class, EndpointHelpers.grabBody(connection))
    accountAnswer[0] == accounts[1l]
  }

  def "GET request should return error if account not present"() {
    given:
    long id = 1
    long userId = 1
    def accounts = new TreeMap()
    accounts[id] = new Account(id, userId)
    setupApi(new TreeMap(), accounts, new TreeMap())
    def connection = new URL(
        "http://$endpoint/api/1.0/accounts/-1"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 404
    def body = transformer.toResource(Error.class, EndpointHelpers.grabBody(connection))
    with(body[0]) {
      errorCode == connection.responseCode
      message == "Account with id -1 not found"
      request == "/api/1.0/accounts/-1"
    }
  }

  def "GET request for transaction sub collection should resolve"() {
    given:
    long fromAccount = 1
    long toAccount = 2
    long userId = 1
    def accountStorage = new TreeMap()
    accountStorage[fromAccount] = new Account(fromAccount, userId)
    accountStorage[toAccount] = new Account(toAccount, userId)

    def transactionStorage = new TreeMap()
    transactionStorage[1L] = new Transaction(1L, fromAccount, toAccount, 0.0)

    setupApi(transactionStorage, accountStorage, new TreeMap())
    def connection = new URL(
        "http://$endpoint/api/1.0/accounts/1/transactions"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 200
    def body = EndpointHelpers.grabBody(connection)
    List<Transaction> transactions = transformer.toResource(Transaction.class, body)
    with(transactions[0]) {
      it.class == Transaction.class
      from.id == fromAccount
      to.id == toAccount
    }
  }

  def "POST request for account creation should process correctly"() {
    given:
    long newAccountId = 42
    long newAccountUserId = 1

    def userStorage = new TreeMap()
    userStorage[1l] = new User(1l)

    def accountStorage = new TreeMap()
    setupApi(new TreeMap(), accountStorage, userStorage)

    def url = new URL("http://$endpoint/api/1.0/accounts/$newAccountId/user/$newAccountUserId")

    when:
    def connection = url.openConnection() as HttpURLConnection
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Accept", "application/yaml")

    then:
    connection.responseCode == 201
    connection.headerFields.get(HttpHeader.LOCATION.asString())[0] == "/api/1" +
        ".0/accounts/1"
    def accounts = transformer.toResource(Account.class, EndpointHelpers.grabBody(connection))
    accounts[0].id != 42
    accounts[0].user.id == newAccountUserId
  }

  def "account creation should work correctly in parallel"() {
    given:
    def newAccountUserId = 1l
    def newAccountId = 1l
    def userStorage = new TreeMap()
    userStorage[1l] = new User(1l)
    def accountStorage = new TreeMap()
    setupApi(new TreeMap(), accountStorage, userStorage)

    when:
    def codes = EndpointHelpers.runParallel(300,
        "http://$endpoint/api/1.0/accounts/$newAccountId/user/$newAccountUserId",
        { it ->
          def location = it.headerFields.get(HttpHeader.LOCATION.asString())[0]
          def id = transformer.toResource(Account.class, EndpointHelpers.grabBody(it))[0].id
          return [it.responseCode, location.endsWith(id.toString())]
        })

    then:
    codes.collect({it[0]}).find({ it != 201 }) == null
    codes.collect({it[1]}).find({ it == false }) == null

  }
}
