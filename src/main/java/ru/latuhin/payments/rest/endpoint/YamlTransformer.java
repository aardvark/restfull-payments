package ru.latuhin.payments.rest.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.StringWriter;
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
    mapper.registerModule(resources);
  }

  public <T> T toResource(Class<T> clz, String yaml) {
    ObjectReader objectReader = mapper.readerFor(clz);
    try {
      return objectReader.readValue(yaml);
    } catch (IOException e) {
      LOGGER.error("Unable to transform yaml to resource: "+ yaml, e);
    }
    return null;
  }

  @Override
  public String render(Object model) {
    if (model == null) {
      return null;
    }
    ObjectWriter objectWriter = mapper.writerFor(model.getClass());
    StringWriter w = new StringWriter();
    try {
      objectWriter.writeValue(w, model);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return w.getBuffer().toString();
  }
}
