package ru.latuhin.revolut.payments.rest.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Account;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.revolut.payments.rest.endpoint.dao.User;
import ru.latuhin.revolut.payments.rest.endpoint.serializers.ResourceSerializer;
import spark.ResponseTransformer;

public class ToYamlResponseTransformer implements ResponseTransformer {
  private final ObjectMapper mapper;

  public ToYamlResponseTransformer() {
    YAMLFactory yamlFactory = new YAMLFactory();
    mapper = new ObjectMapper(yamlFactory);

    SimpleModule resources = new SimpleModule();
    Stream.of(User.class, Transaction.class, Account.class).forEach(
        c -> resources.addSerializer(c, new ResourceSerializer<>())
    );
    mapper.registerModule(resources);
  }

  @Override
  public String render(Object model) {
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
