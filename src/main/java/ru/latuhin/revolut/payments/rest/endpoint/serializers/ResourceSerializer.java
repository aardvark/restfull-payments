package ru.latuhin.revolut.payments.rest.endpoint.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Account;

public class ResourceSerializer<T extends SerializableResource> extends StdSerializer<T> {

  public ResourceSerializer() {
    this(null);
  }

  protected ResourceSerializer(Class<T> t) {
    super(t);
  }

  @Override
  public void serialize(T value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    value.serialize(gen);

  }
}
