package io.wttech.habit.server.environment.requestgraph.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.wttech.habit.client.requestgraph.RequestGraph;
import io.wttech.habit.client.requestgraph.RequestGraphFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class RequestGraphFactoryTest {

  @Test
  public void create() {
    RequestGraphFactory tested = RequestGraphFactory.instance();
    String graphString = loadFileContent("/requestGraph.json");

    RequestGraph requestGraph = tested.create(graphString);

    RequestGraph firstChildRequest = requestGraph.getFirstChild();
    assertNotNull(firstChildRequest);

    RequestGraph grandChildRequest = firstChildRequest.getFirstChild();
    assertNotNull(grandChildRequest);
  }

  private String loadFileContent(String resource) {
    InputStream stream = RequestGraphFactoryTest.class.getResourceAsStream(resource);
    return asString(stream);
  }

  private String asString(InputStream inputStream) {
    try {
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }
      return result.toString(StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
