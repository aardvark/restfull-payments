package ru.latuhin.payments.rest.endpoint.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import java.io.IOException;
import java.math.BigDecimal;
import ru.latuhin.payments.rest.endpoint.dao.Status;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;

public class TransactionDeserializer extends StdDeserializer<Transaction> {

  public TransactionDeserializer() {
    this(null);
  }

  public TransactionDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Transaction deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    TreeNode treeNode = p.getCodec().readTree(p);
    IntNode id = (IntNode) treeNode.get("id");
    IntNode from = (IntNode) treeNode.get("from").get("id");
    IntNode to = (IntNode) treeNode.get("to").get("id");
    TreeNode amountNode = treeNode.get("amount");
    BigDecimal amount;
    if (amountNode instanceof IntNode) {
      amount = new BigDecimal(((IntNode) amountNode).asText());
    } else if (amountNode instanceof DoubleNode) {
      amount = new BigDecimal(((DoubleNode) amountNode).asText());
    } else {
      amount = null;
    }

    return new Transaction(id.longValue(), from.longValue(), to.longValue(), amount, Status.Open);
  }
}
