package io.wttech.habit.client.requestgraph.assertion.response;

import io.wttech.habit.client.requestgraph.assertion.StringAssertion;
import java.util.function.Consumer;
import java.util.function.Function;

public class ReasonAssertion extends StringAssertion {

  ReasonAssertion(String value) {
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
   * reasonAssertion.verify(ReasonsAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<ReasonAssertion> consumer) {
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
  public <T> T verify(Function<ReasonAssertion, T> consumer) {
    return consumer.apply(this);
  }
}
