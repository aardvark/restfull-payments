package ru.latuhin.payments.rest.endpoint.dao;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.latuhin.payments.rest.endpoint.serializers.SerializableResource;

/**
 * Yaml example:
 * <pre>{@code
 *  !user
 *  id: {id}
 *  href: /api/users/{id}
 *  accounts:
 *    rel: collection/accounts
 *    href: /api/users/{id}/accounts
 *  transactions:
 *    rel: collection/transactions
 *    href: /api/users/{id}/transactions
 * }</pre>
 */
public class User implements SerializableResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

  public final long id;
  public final LinkCollection accounts;
  public final LinkCollection transactions;

  public User(long id) {
    this.id = id;
    accounts = new LinkCollection("accounts", id);
    transactions = new LinkCollection("transactions", id);
  }

  public void serialize(JsonGenerator gen) {
    try {
      gen.writeTypeId("user");
      gen.writeStartObject();
      gen.writeNumberField("id", this.id);
      gen.writeStringField("href", "/api/users/" + this.id);
      gen.writeObjectField("accounts", this.accounts);
      gen.writeObjectField("transactions", this.transactions);
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
    User user = (User) o;
    return id == user.id &&
        Objects.equals(accounts, user.accounts) &&
        Objects.equals(transactions, user.transactions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, accounts, transactions);
  }
}
