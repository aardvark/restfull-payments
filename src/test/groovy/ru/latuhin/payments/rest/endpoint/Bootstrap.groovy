package ru.latuhin.payments.rest.endpoint

import spock.lang.Specification

class BootstrapTest extends Specification {
  def "create users"() {
    given:
    def bootstrap = new Bootstrap()
    def users = bootstrap.createUsers()

    expect:
    users.keySet() == (0..9).collect() as Set
  }

  def "create accounts"() {
    given:
    def bootstrap = new Bootstrap()
    def accounts = bootstrap.createAccounts()

    expect:
    accounts.keySet() == (0..29).collect() as Set
  }
}
