package ru.latuhin.payments.rest.endpoint

import spock.lang.Specification
import spock.lang.Unroll

class LauncherTest extends Specification {
/*  def "port can be configured through system property"() {
    given:
    System.setProperty('api.bind.port', '7890')
    Launcher.main(null)
    def url = "http://localhost:7890/hearthbeat"

    when:
    def connection = EndpointHelpers.get(url)

    then:
    connection.responseCode == 200
    def body = EndpointHelpers.grabBody(connection)
    body == 'live'
  }*/

  @Unroll
  def "launcher correctly bootstrap application"() {
    given:
    def bootstrap = Mock(Bootstrap)
    Launcher.bootstrap = bootstrap

    when:
    Launcher.main(["bootstrap"] as String[])

    then:
    1 * bootstrap.createAccounts()
    1 * bootstrap.createUsers()

  }
}
