package io.wttech.habit.client.requestgraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.UncheckedIOException;

public class RequestTestFactory {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());;

  private final RequestGraphFactory factory;

  RequestTestFactory(RequestGraphFactory factory) {
    this.factory = factory;
  }

  public static RequestTestFactory instance() {
    return new RequestTestFactory(RequestGraphFactory.instance());
  }

  public RequestTest create(String json) {
    try {
      RequestTestModel model = objectMapper.readValue(json, RequestTestModel.class);
      return create(model);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public RequestTest create(RequestTestModel model) {
    return RequestTest.builder()
        .withId(model.getId())
        .withEnvironmentId(model.getEnvironmentId())
        .withStart(model.getStart())
        .withEnd(model.getEnd())
        .withGraph(factory.create(model.getGraph()))
        .build();
  }

}
