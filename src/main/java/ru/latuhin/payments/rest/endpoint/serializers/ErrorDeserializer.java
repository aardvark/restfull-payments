package ru.latuhin.payments.rest.endpoint.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import ru.latuhin.payments.rest.endpoint.dao.Error;

public class ErrorDeserializer extends StdDeserializer<Error> {

  public ErrorDeserializer() {
    this(null);
  }

  public ErrorDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Error deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    TreeNode treeNode = p.getCodec().readTree(p);
    TextNode requestPath = (TextNode) treeNode.get("request");
    IntNode errorCode = (IntNode) treeNode.get("code");
    TextNode message = (TextNode) treeNode.get("message");
    return new Error(requestPath.textValue(), errorCode.intValue(), message.textValue());
  }
}
