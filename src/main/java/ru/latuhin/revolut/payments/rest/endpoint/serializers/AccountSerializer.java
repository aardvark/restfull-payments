package ru.latuhin.revolut.payments.rest.endpoint.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Account;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;

public class AccountSerializer extends StdSerializer<Account> {

  public AccountSerializer() {
    this(null);
  }

  protected AccountSerializer(Class<Account> t) {
    super(t);
  }

  @Override
  public void serialize(Account obj, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    obj.serialize(gen);
  }
}
