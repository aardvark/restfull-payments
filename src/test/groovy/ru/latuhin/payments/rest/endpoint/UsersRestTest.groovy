package ru.latuhin.payments.rest.endpoint

import spock.lang.Specification

class UsersRestTest extends Specification {
  def endpoint = 'localhost:4567'

    def "GET request should resolve"() {
    given:
    long id = 1
    def app = new App()
    app.setStorage(new TreeMap(), new HashMap())
    app.setup()
    def connection = new URL(
        "http://$endpoint/api/1.0/users/$id"
    ).openConnection() as HttpURLConnection

    expect:
    connection.responseCode == 200
  }

}
