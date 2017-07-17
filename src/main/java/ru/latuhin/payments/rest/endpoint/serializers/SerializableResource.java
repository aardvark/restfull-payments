package ru.latuhin.payments.rest.endpoint.serializers;

import com.fasterxml.jackson.core.JsonGenerator;

public interface SerializableResource {
  void serialize(JsonGenerator gen);
}
