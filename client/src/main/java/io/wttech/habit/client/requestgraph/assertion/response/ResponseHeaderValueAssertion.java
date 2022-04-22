package io.wttech.habit.client.requestgraph.assertion.response;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.ExchangeHeader;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.AbstractStringAssert;

public class ResponseHeaderValueAssertion {

  private static final String ASSERTION_STRING_FORMAT = "Verifying response header <%s> value";

  private final ExchangeHeader header;

  public ResponseHeaderValueAssertion(ExchangeHeader header) {
    this.header = header;
  }

  public void is(String value) {
    assertThat(header.getValue())
        .as(ASSERTION_STRING_FORMAT, header.getName())
        .isEqualTo(value);
  }

  public void isBlank() {
    assertThat(header.getValue())
        .as(ASSERTION_STRING_FORMAT, header.getName())
        .isBlank();
  }

  public void isNotBlank() {
    assertThat(header.getValue())
        .as(ASSERTION_STRING_FORMAT, header.getName())
        .isNotBlank();
  }

  public void isEmpty() {
    assertThat(header.getValue())
        .as(ASSERTION_STRING_FORMAT, header.getName())
        .isEmpty();
  }

  public void isNotEmpty() {
    assertThat(header.getValue())
        .as(ASSERTION_STRING_FORMAT, header.getName())
        .isNotEmpty();
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions on header value.
   *
   * @return AssertJ string assertion
   */
  public AbstractStringAssert<?> asAssert() {
    return assertThat(header.getValue());
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
   * valueAssertion.verify(HeaderAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<ResponseHeaderValueAssertion> consumer) {
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
  public <T> T verify(Function<ResponseHeaderValueAssertion, T> consumer) {
    return consumer.apply(this);
  }

}
