package ru.latuhin.payments.rest.endpoint

import ru.latuhin.payments.rest.endpoint.dao.Account
import ru.latuhin.payments.rest.endpoint.dao.Error
import ru.latuhin.payments.rest.endpoint.dao.Transaction
import ru.latuhin.payments.rest.endpoint.dao.User
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static ru.latuhin.payments.rest.endpoint.EndpointHelpers.grabBody
import static ru.latuhin.payments.rest.endpoint.EndpointHelpers.setupApi

@Unroll
class UsersRestTest extends Specification {
  @Shared
  def static endpoint = 'localhost:4567'
  def transformer = new YamlTransformer()

  def "GET request should resolve for #url"() {
    given:
    setupApi(new TreeMap(), [:], [22l: new User(22l)])

    when:
    def connection = EndpointHelpers.get(url)

    then:
    connection.responseCode == 200
    def resource = transformer.toResource(clz, grabBody(connection))
    resource.class == clz
    where:
    url                                              | clz
    "http://$endpoint/api/1.0/users/22"              | User.class
    "http://$endpoint/api/1.0/users/22/accounts"     | ArrayList.class
    "http://$endpoint/api/1.0/users/22/transactions" | ArrayList.class
  }

  def "returned user should match one stored in the storage"() {
    given:
    Map<Long, User> userStorage = new HashMap<>()
    def storageUserId = 43l
    def url = "http://$endpoint/api/1.0/users/$storageUserId"
    userStorage[storageUserId] = new User(storageUserId)
    setupApi(new TreeMap(), [:], userStorage)

    when:
    def connection = EndpointHelpers.get(url)
    User user = transformer.toResource(User.class, grabBody(connection))

    then:
    connection.responseCode == 200
    user.id == storageUserId
  }

  def "missing user should correctly return error"() {
    given:
    Map<Long, User> userStorage = new HashMap<>()
    def url = "http://$endpoint/api/1.0/users/22"
    setupApi(new TreeMap(), [:], userStorage)

    when:
    def connection = EndpointHelpers.get(url)

    then:
    connection.responseCode == 404
    Error error = transformer.toResource(Error.class, grabBody(connection))
    error.message == 'User with id 22 not found'
  }


  def "returned accounts should match one stored in the storage"() {
    given:
    def userId = 22l
    def userStorage = [(userId): new User(userId)]
    def accountStorage = [(15l): new Account(15l, userId),
                          (10l): new Account(10l, userId)]
    setupApi(new TreeMap(), accountStorage, userStorage)
    def url = "http://$endpoint/api/1.0/users/22/accounts"

    when:
    def connection = EndpointHelpers.get(url)
    List<Account> accounts = transformer.toResource(Account.class, grabBody(connection))

    then:
    connection.responseCode == 200
    accounts.size() == accountStorage.values().findAll({ it.user.id == userId }).size()
    accounts[0] == accountStorage.values()[0]
  }

  def "returned transactions should match one stored in the storage"() {
    given:
    def requestUserId = 22l
    def otherUserId = 42l
    def userStorage = [(requestUserId): new User(requestUserId), (otherUserId): new User(otherUserId)]
    def accountId = 15l
    def otherUserAccountId = 682l
    def accountStorage = [(accountId)         : new Account(accountId, requestUserId),
                          (otherUserAccountId): new Account(otherUserAccountId, otherUserId)]
    def transactionStorage = new TreeMap()
    transactionStorage[1l] = new Transaction(1l, accountId, accountId, new BigDecimal(0.0))
    transactionStorage[2l] = new Transaction(2l, accountId, accountId, new BigDecimal(10.0))
    transactionStorage[3l] = new Transaction(3l, accountId, otherUserAccountId, new BigDecimal(16.0))
    transactionStorage[4l] = new Transaction(4l, otherUserAccountId, accountId, new BigDecimal(16.0))

    def url = "http://$endpoint/api/1.0/users/22/transactions"
    setupApi(transactionStorage, accountStorage, userStorage)

    when:
    def connection = EndpointHelpers.get(url)
    List<Transaction> transactions = transformer.toResource(Transaction.class, grabBody(connection))

    then:
    connection.responseCode == 200
    transactions.size() == 3
  }
}
