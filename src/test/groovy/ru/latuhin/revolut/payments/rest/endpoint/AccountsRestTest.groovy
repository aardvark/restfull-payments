package ru.latuhin.revolut.payments.rest.endpoint

import ru.latuhin.revolut.payments.rest.endpoint.dao.Account
import spock.lang.Specification

import java.util.stream.Collectors

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
    def s = connection.inputStream.withCloseable { iStream ->
      new BufferedReader(new InputStreamReader(iStream))
          .lines().collect(Collectors.joining("\n"))
    }
    def accountAnswer = transformer.toResource(Account.class, s)
    accountAnswer == accounts[1l]
  }
}
