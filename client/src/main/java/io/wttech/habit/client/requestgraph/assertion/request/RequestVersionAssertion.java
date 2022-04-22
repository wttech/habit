package io.wttech.habit.client.requestgraph.assertion.request;

import io.wttech.habit.client.requestgraph.assertion.StringAssertion;
import java.util.function.Consumer;
import java.util.function.Function;

public class RequestVersionAssertion extends StringAssertion {

  RequestVersionAssertion(String value) {
    super(value);
  }

  /**
   * <p>
   * Assertion DSL extension mechanism for Java.
   *
   * <p>
   * While Java does not support extension methods like Kotlin it is still possible to provide a
   * somewhat similar syntax for custom assertions.
   *
   * <p>
   * Example:
   *
   * <p>
   * <code>
   * requestVersionAssertion.verify(RquestVersionAssertions::isHttp11)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<RequestVersionAssertion> consumer) {
    consumer.accept(this);
  }

  /**
   * <p>
   * Assertion DSL extension mechanism for Java.
   *
   * <p>
   * Variant of {@link #verify(Consumer)} allowing for assertion chaining.
   *
   * @param consumer function returning desired subgraph
   * @param <T> type returned by the provided function
   * @return object returned by the provided function
   */
  public <T> T verify(Function<RequestVersionAssertion, T> consumer) {
    return consumer.apply(this);
  }

}
