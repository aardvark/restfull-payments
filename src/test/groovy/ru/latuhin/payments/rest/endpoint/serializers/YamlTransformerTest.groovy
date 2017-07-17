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
}
