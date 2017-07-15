package ru.latuhin.revolut.payments.rest.endpoint.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;

public class TransactionSerializer extends StdSerializer<Transaction> {

  public TransactionSerializer() {
    this(null);
  }

  protected TransactionSerializer(Class<Transaction> t) {
    super(t);
  }

  @Override
  public void serialize(Transaction obj, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    obj.serialize(gen);
  }
}
