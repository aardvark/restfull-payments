package ru.latuhin.payments.rest.endpoint.dao;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.latuhin.payments.rest.endpoint.serializers.SerializableResource;

/**
 * <pre>{@code
 * !account
 * id: {id}
 * href: /api/accounts/{id}
 * user:
 *   id:{userId}
 *   rel: resource/user
 *   href: /api/users/{userId}
 * transactions:
 *   rel: collection/transactions
 *   href: /api/accounts/{id}/transactions
 * amount: {amount}
 * }</pre>
 */

public class Account implements SerializableResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(Account.class);

  public final long id;
  public final LinkResource user;
  public final LinkSubCollection transactions;
  public final BigDecimal amount;

  public Account(long id, long userId) {
    this.id = id;
    this.user = new LinkResource("users", userId);
    this.transactions = new LinkSubCollection("accounts", id, "transactions");
    this.amount = BigDecimal.ZERO;
  }

  public Account(long id, long userId, BigDecimal amount) {
    this.id = id;
    this.user = new LinkResource("users", userId);
    this.transactions = new LinkSubCollection("accounts", id, "transactions");
    this.amount = amount;
  }

  public Account(Account fromAccount, BigDecimal amount) {
    this(fromAccount.id, fromAccount.user.id, fromAccount.amount.subtract(amount));

  }

  public void serialize(JsonGenerator gen) {
    try {
      gen.writeTypeId("account");
      gen.writeStartObject();
      gen.writeNumberField("id", this.id);
      gen.writeStringField("href", "/api/account/" + this.id);
      gen.writeObjectField("user", this.user);
      gen.writeObjectField("transactions", this.transactions);
      gen.writeNumberField("amount", this.amount.doubleValue());
      gen.writeEndObject();
    } catch (IOException e) {
      LOGGER.error("Unable to serialize " + getClass() + " due to:", e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Account account = (Account) o;
    return id == account.id &&
        Objects.equals(user, account.user) &&
        Objects.equals(transactions, account.transactions) &&
        Objects.equals(amount, account.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, user, transactions, amount);
  }

  @Override
  public String toString() {
    return "Account{" +
        "id=" + id +
        ", amount=" + amount +
        '}';
  }

  public boolean matchByUser(long id) {
    return this.user.id == id;
  }
}
