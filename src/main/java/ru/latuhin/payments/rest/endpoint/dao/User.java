package ru.latuhin.payments.rest.endpoint.dao;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
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
}
