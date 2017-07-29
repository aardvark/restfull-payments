package ru.latuhin.payments.rest.endpoint.serializers

import ru.latuhin.payments.rest.endpoint.YamlTransformer
import ru.latuhin.payments.rest.endpoint.dao.Transaction
import spock.lang.Specification

class YamlTransformerTest extends Specification {
  def "ToResource"() {
    given:
    def transformer = new YamlTransformer()
    Transaction transaction = transformer.toResource(Transaction.class, '''--- !<transaction>
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
''')
    expect:
    transaction.id == 1
    transaction.from.id == 2
    transaction.to.id == 3
    transaction.amount == 10.5

  }

  def "list to resource"() {
    given:
    def yaml = '''---
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
    def transformer = new YamlTransformer()
    def list = transformer.toResource(Transaction.class, yaml)

    expect:
    list.size() == 2
    list[0] == new Transaction(0,0,0, 0.0)
    list[1] == new Transaction(1,1,1,0.0)
  }
}
