package io.wttech.habit.client.requestgraph.assertion.request;

import io.wttech.habit.client.requestgraph.assertion.StringAssertion;
import java.util.function.Consumer;
import java.util.function.Function;

public class MethodAssertion extends StringAssertion {

  MethodAssertion(String value) {
    super(value);
  }

  public void isGet() {
    asAssert().isEqualTo("GET");
  }

  public void isPost() {
    asAssert().isEqualTo("POST");
  }

  public void isPut() {
    asAssert().isEqualTo("PUT");
  }

  public void isDelete() {
    asAssert().isEqualTo("DELETE");
  }

  public void isPatch() {
    asAssert().isEqualTo("PATCH");
  }

  public void isHead() {
    asAssert().isEqualTo("HEAD");
  }

  public void isConnect() {
    asAssert().isEqualTo("CONNECT");
  }

  public void isOptions() {
    asAssert().isEqualTo("OPTIONS");
  }

  public void isTrace() {
    asAssert().isEqualTo("TRACE");
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
   * methodAssertion.verify(MethodAssertions::isWebDav)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<MethodAssertion> consumer) {
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
  public <T> T verify(Function<MethodAssertion, T> consumer) {
    return consumer.apply(this);
  }

}
