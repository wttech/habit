package io.wttech.habit.client.requestgraph.assertion.request;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.ExchangeHeader;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.AbstractStringAssert;

public class RequestHeaderValueAssertion {

  private static final String VALUE_ASSERTION_STRING_FORMAT = "Verifying request header <%s> value";

  private final ExchangeHeader header;

  public RequestHeaderValueAssertion(ExchangeHeader header) {
    this.header = header;
  }

  public void is(String value) {
    asAssert().isEqualTo(value);
  }

  public void isBlank() {
    asAssert().isBlank();
  }

  public void isNotBlank() {
    asAssert().isNotBlank();
  }

  public void isEmpty() {
    asAssert().isEmpty();
  }

  public void isNotEmpty() {
    asAssert().isNotEmpty();
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions on this header
   * value.
   *
   * @return AssertJ string assertion
   */
  public AbstractStringAssert<?> asAssert() {
    return assertThat(header.getValue())
        .as(VALUE_ASSERTION_STRING_FORMAT, header.getName());
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
   * requestHeaderValueAssertion.verify(HeaderAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<RequestHeaderValueAssertion> consumer) {
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
  public <T> T verify(Function<RequestHeaderValueAssertion, T> consumer) {
    return consumer.apply(this);
  }

}
