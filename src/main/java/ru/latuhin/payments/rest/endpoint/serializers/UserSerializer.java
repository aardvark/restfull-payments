package ru.latuhin.payments.rest.endpoint.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import ru.latuhin.payments.rest.endpoint.dao.User;

public class UserSerializer extends StdSerializer<User> {

  public UserSerializer() {
    this(null);
  }

  protected UserSerializer(Class<User> t) {
    super(t);
  }

  @Override
  public void serialize(User user, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    user.serialize(gen);
  }
}
