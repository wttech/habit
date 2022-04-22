package io.wttech.habit.junit;

import io.wttech.habit.client.http.HabitHttpClient;
import io.wttech.habit.client.request.specification.RequestDefinitionDSL;

import java.util.function.Consumer;

public class HabitRequestDSL {

  private final String environmentId;
  private final HabitHttpClient client;

  HabitRequestDSL(String environmentId,
      HabitHttpClient client) {
    this.environmentId = environmentId;
    this.client = client;
  }

  public static HabitRequestDSL of(String environmentId,
      HabitHttpClient client) {
    return new HabitRequestDSL(environmentId, client);
  }

  /**
   * <p>
   * Creates a new test request builder.
   *
   * @return DSL for further customization
   */
  public RequestDefinitionDSL request() {
    return RequestDefinitionDSL.of(environmentId, client);
  }

  /**
   * <p>
   * Creates a new test request builder customizer already applied.
   * <p>
   * Shorthand for request().apply(customizer)
   *
   * @param customizer consumer customizing the test request
   * @return DSL for further customization
   */
  public RequestDefinitionDSL request(Consumer<RequestDefinitionDSL> customizer) {
    return request().apply(customizer);
  }

}
