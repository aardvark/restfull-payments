package ru.latuhin.payments.rest.endpoint

import ru.latuhin.payments.rest.endpoint.dao.Account
import ru.latuhin.payments.rest.endpoint.dao.Error
import ru.latuhin.payments.rest.endpoint.dao.Transaction
import spock.lang.Specification

class AccountsRestTest extends Specification {
  String endpoint = 'localhost:4567'
  YamlTransformer transformer = new YamlTransformer()

  def "GET request should resolve"() {
    given:
    long id = 1
    long userId = 1
    def accounts = new HashMap()
    accounts[id] = new Account(id, userId)
    def app = new App()
    app.setStorage([:] as NavigableMap, accounts)
    App.main(null)
    def connection = new URL(
        "http://$endpoint/api/1.0/accounts/$id"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 200
    def accountAnswer = transformer.toResource(Account.class, EndpointHelpers.grabBody(connection))
    accountAnswer == accounts[1l]
  }

  def "GET request should return error if account not present"() {
    given:
    long id = 1
    long userId = 1
    def accounts = new HashMap()
    accounts[id] = new Account(id, userId)
    def app = new App()
    app.setStorage([:] as NavigableMap, accounts)
    App.main(null)
    def connection = new URL(
        "http://$endpoint/api/1.0/accounts/-1"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 404
    Error body = transformer.toResource(Error.class, EndpointHelpers.grabBody(connection))
    with(body) {
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

    def app = new App()
    app.setStorage(transactionStorage, accountStorage)
    App.main(null)
    def connection = new URL(
        "http://$endpoint/api/1.0/accounts/1/transactions"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 200
    def body = EndpointHelpers.grabBody(connection)
    List<Transaction> transactions = transformer.toResource(List.class, body)
    with (transactions[0]) {
      it.class == Transaction.class
      from.id == fromAccount
      to.id == toAccount
    }
  }
}
