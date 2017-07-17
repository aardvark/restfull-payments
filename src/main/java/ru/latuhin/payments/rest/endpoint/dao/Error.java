package ru.latuhin.payments.rest.endpoint.dao;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.Objects;
import ru.latuhin.payments.rest.endpoint.serializers.SerializableResource;

/**
 * <pre>{@code
 * !error
 * request: {request}
 * code: {errorCode}
 * message: {message}
 * }</pre>
 */
public class Error implements SerializableResource {

  public String request;
  public int errorCode;
  public String message;

  public Error(String request, int errorCode, String message) {
    this.request = request;
    this.errorCode = errorCode;
    this.message = message;
  }

  @Override
  public void serialize(JsonGenerator gen) {
    try {
      gen.writeTypeId("error");
      gen.writeStartObject();
      gen.writeStringField("request", request);
      gen.writeNumberField("code", errorCode);
      gen.writeStringField("message", message);
      gen.writeEndObject();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Error error = (Error) o;
    return errorCode == error.errorCode &&
        Objects.equals(request, error.request) &&
        Objects.equals(message, error.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(request, errorCode, message);
  }
}
