package ru.latuhin.payments.rest.endpoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.latuhin.payments.rest.endpoint.dao.Account;
import ru.latuhin.payments.rest.endpoint.dao.Error;
import ru.latuhin.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.payments.rest.endpoint.dao.User;
import ru.latuhin.payments.rest.endpoint.serializers.AccountDeserializer;
import ru.latuhin.payments.rest.endpoint.serializers.ErrorDeserializer;
import ru.latuhin.payments.rest.endpoint.serializers.ResourceSerializer;
import ru.latuhin.payments.rest.endpoint.serializers.TransactionDeserializer;
import ru.latuhin.payments.rest.endpoint.serializers.UserDeserializer;
import spark.Response;
import spark.ResponseTransformer;

public class YamlTransformer implements ResponseTransformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(YamlTransformer.class);

  private final ObjectMapper mapper;

  public YamlTransformer() {
    YAMLFactory yamlFactory = new YAMLFactory();
    mapper = new ObjectMapper(yamlFactory);

    SimpleModule resources = new SimpleModule();
    Stream.of(User.class, Transaction.class, Account.class, Error.class).forEach(
        c -> resources.addSerializer(c, new ResourceSerializer<>())
    );
    resources.addDeserializer(Transaction.class, new TransactionDeserializer());
    resources.addDeserializer(Account.class, new AccountDeserializer());
    resources.addDeserializer(Error.class, new ErrorDeserializer());
    resources.addDeserializer(User.class, new UserDeserializer());
    mapper.registerModule(resources);
  }

  public <T> List<T> toResource(Class<T> clz, String yaml) {
    try {
      JsonNode node = mapper.readTree(yaml);
      if (node.isArray()) {
        List<T> list = mapper
            .readValue(yaml, mapper.getTypeFactory().constructCollectionType(List.class, clz));
        if (list.isEmpty()) {
          return Collections.emptyList();
        }
        return list;
      }
      return Collections.singletonList(mapper.readValue(yaml, clz));
    } catch (IOException e) {
      LOGGER.error("Unable to transform yaml to resource (" + clz + "): " + yaml, e);
    }
    return null;
  }

  @Override
  public String render(Object model) {
    if (model == null) {
      return null;
    }

    if (model instanceof Response) {
      return "";
    }

    try {
      return mapper.writeValueAsString(model);
    } catch (IOException e) {
      LOGGER.error("Unable to render response model due to:", e);
    }
    return null;
  }
}
