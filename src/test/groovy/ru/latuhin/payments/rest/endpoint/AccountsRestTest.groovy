package ru.latuhin.payments.rest.endpoint

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
    app.setStorage(new TreeMap(), [:], new TreeMap<Long, User>())
    app.setup()
  }

  def cleanupSpec() {
    app.stop()
  }

  def "GET request should resolve"() {
    given:
    long id = 1
    long userId = 1
    def accounts = new HashMap()
    accounts[id] = new Account(id, userId)
    setupApi(new TreeMap(), accounts, [:])
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
    def accounts = new HashMap()
    accounts[id] = new Account(id, userId)
    setupApi(new TreeMap(), accounts, [:])
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
    def accountStorage = new HashMap()
    accountStorage[fromAccount] = new Account(fromAccount, userId)
    accountStorage[toAccount] = new Account(toAccount, userId)

    def transactionStorage = new TreeMap()
    transactionStorage[1L] = new Transaction(1L, fromAccount, toAccount, 0.0)

    setupApi(transactionStorage, accountStorage, [:])
    def connection = new URL(
        "http://$endpoint/api/1.0/accounts/1/transactions"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 200
    def body = EndpointHelpers.grabBody(connection)
    List<Transaction> transactions = transformer.toResource(Transaction.class, body)
    with (transactions[0]) {
      it.class == Transaction.class
      from.id == fromAccount
      to.id == toAccount
    }
  }


}
