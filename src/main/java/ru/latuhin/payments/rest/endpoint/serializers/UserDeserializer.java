package ru.latuhin.payments.rest.endpoint.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import java.io.IOException;
import ru.latuhin.payments.rest.endpoint.dao.User;

public class UserDeserializer extends StdDeserializer<User> {

  public UserDeserializer() {
    this(null);
  }

  public UserDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public User deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    TreeNode treeNode = p.getCodec().readTree(p);
    IntNode id = (IntNode) treeNode.get("id");
    return new User(id.longValue());
  }
}
