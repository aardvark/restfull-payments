package ru.latuhin.payments.rest.endpoint.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import java.io.IOException;
import java.math.BigDecimal;
import ru.latuhin.payments.rest.endpoint.dao.Account;

public class AccountDeserializer extends StdDeserializer<Account> {

  public AccountDeserializer() {
    this(null);
  }

  public AccountDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Account deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    TreeNode treeNode = p.getCodec().readTree(p);
    IntNode id = (IntNode) treeNode.get("id");
    IntNode userId = (IntNode) treeNode.get("user").get("id");
    BigDecimal amount = new BigDecimal(0.0);
    return new Account(id.longValue(), userId.longValue(), amount);
  }
}
