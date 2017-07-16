package ru.latuhin.revolut.payments.rest.endpoint.dao;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import ru.latuhin.revolut.payments.rest.endpoint.serializers.SerializableResource;

/**
 * <pre>{@code
 * object Transaction {
 *   id
 *   from: Account
 *   to: Account
 *   amount: Amount
 *   status: Status
 *   allows: GET, PUT, DELETE, OPTIONS
 * }
 * }</pre>
 * Yaml example: <pre>{@code
 * !transaction
 * id: {id}
 * href: /api/transactions/{id}
 * from:
 *   href: /api/accounts/{from}
 * to:
 *   href: /api/accounts/{to}
 * amount: {amount}
 * status:
 *   href: /api/transactions/{id}/status
 * }</pre>
 *
 */
public class Transaction implements SerializableResource {
  public long id;
  public LinkResource from;
  public LinkResource to;
  public BigDecimal amount;
  public Status status;

  public Transaction(){}

  public Transaction(long id, long from, long to, BigDecimal amount,
      Status status) {
    this.id = id;
    this.from = new LinkResource("account", from);
    this.to = new LinkResource("account", to);
    this.amount = amount;
    this.status = status;
  }

  public Transaction(long id, long from, long to, BigDecimal amount) {
    this(id, from, to, amount, Status.Open);
  }

  public void serialize(JsonGenerator gen) {
    try {
      gen.writeTypeId("transaction");
      gen.writeStartObject();
      gen.writeNumberField("id", this.id);
      gen.writeStringField("href", "/api/transactions/" + this.id);
      gen.writeObjectField("from", this.from);
      gen.writeObjectField("to", this.to);
      gen.writeNumberField("amount", this.amount);
      gen.writeEndObject();
    } catch (IOException e) {
      e.printStackTrace();
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
    Transaction that = (Transaction) o;
    return id == that.id &&
        Objects.equals(from, that.from) &&
        Objects.equals(to, that.to) &&
        Objects.equals(amount, that.amount) &&
        status == that.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, from, to, amount, status);
  }

  @Override
  public String toString() {
    return "Transaction{" +
        "id=" + id +
        '}';
  }
}
