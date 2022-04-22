package io.wttech.habit.client.requestgraph.assertion.response;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.ExchangeHeader;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.ObjectAssert;

public class ResponseHeaderAssertion {

  private final ExchangeHeader header;

  ResponseHeaderAssertion(ExchangeHeader header) {
    this.header = header;
  }

  /**
   * Asserts the presence of this response header.
   */
  public void isPresent() {
    assertThat(header.isPresent())
        .withFailMessage("Header <%s> is not present", header.getName())
        .isTrue();
  }

  /**
   * Asserts the absence of this response header.
   */
  public void isAbsent() {
    assertThat(header.isAbsent())
        .withFailMessage("Header <%s> is present", header.getName())
        .isTrue();
  }

  /**
   * Asserts response header presence and retrieves assertion DSL for this header value.
   *
   * @return header value assertion
   */
  public ResponseHeaderValueAssertion value() {
    isPresent();
    return new ResponseHeaderValueAssertion(header);
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions on this response
   * header.
   *
   * @return AssertJ object assertion
   */
  public ObjectAssert<ExchangeHeader> asAssert() {
    return assertThat(header);
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
   * responseHeaderAssertion.verify(HeaderAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<ResponseHeaderAssertion> consumer) {
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
  public <T> T verify(Function<ResponseHeaderAssertion, T> consumer) {
    return consumer.apply(this);
  }
}
