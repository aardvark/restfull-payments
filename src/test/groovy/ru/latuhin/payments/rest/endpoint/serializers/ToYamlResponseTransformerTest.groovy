package ru.latuhin.payments.rest.endpoint.serializers

import ru.latuhin.payments.rest.endpoint.YamlTransformer
import ru.latuhin.payments.rest.endpoint.dao.*
import spock.lang.Specification

class ToYamlResponseTransformerTest extends Specification {
  def "user"() {
    given:
    def transformer = new YamlTransformer()
    def result = transformer.render(new User(2))

    expect:
    result != null
    result == '''--- !<user>
id: 2
href: "/api/users/2"
accounts:
  rel: "collection/accounts"
  href: "/api/users/2/accounts"
transactions:
  rel: "collection/transactions"
  href: "/api/users/2/transactions"
'''

  }

  def "account"() {
    given:
    def transformer = new YamlTransformer()
    def result = transformer.render(new Account(0, 1))

    expect:
    result != null
    result == '''--- !<account>
id: 0
href: "/api/account/0"
user:
  id: 1
  rel: "resource/users"
  href: "/api/users/1"
transactions:
  rel: "collection/transactions"
  href: "/api/accounts/0/transactions"
amount: 0.0
'''
  }

  def "transaction"() {
    given:
    def transformer = new YamlTransformer()
    def result = transformer.render(new Transaction(1, 2, 3, new BigDecimal(10.5), Status.Open))

    expect:
    result != null
    result == '''--- !<transaction>
id: 1
href: "/api/transactions/1"
from:
  id: 2
  rel: "resource/account"
  href: "/api/account/2"
to:
  id: 3
  rel: "resource/account"
  href: "/api/account/3"
amount: 10.5
'''
  }

  def "error"() {
    given:
    def transformer = new YamlTransformer()
    def result = transformer.render(new Error("/api/1.0/something/2", 404, "Unable to find"))

    expect:
    result != null
    result == '''--- !<error>
request: "/api/1.0/something/2"
code: 404
message: "Unable to find"
'''
  }

  def "list"() {
    def transformer = new YamlTransformer()
    def result = transformer.render([new Transaction(0, 0, 0, 0.0),
                                     new Transaction(1, 1, 1, 0.0)])

    expect:
    result != null
    result == '''---
- !<transaction>
  id: 0
  href: "/api/transactions/0"
  from:
    id: 0
    rel: "resource/account"
    href: "/api/account/0"
  to:
    id: 0
    rel: "resource/account"
    href: "/api/account/0"
  amount: 0.0
- !<transaction>
  id: 1
  href: "/api/transactions/1"
  from:
    id: 1
    rel: "resource/account"
    href: "/api/account/1"
  to:
    id: 1
    rel: "resource/account"
    href: "/api/account/1"
  amount: 0.0
'''
  }

}
