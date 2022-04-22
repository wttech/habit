package io.wttech.habit.client.requestgraph.assertion.request;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.ExchangeHeader;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.ObjectAssert;

public class RequestHeaderAssertion {

  private final ExchangeHeader header;

  RequestHeaderAssertion(ExchangeHeader header) {
    this.header = header;
  }

  public void isPresent() {
    assertThat(header.isPresent())
        .withFailMessage("Header <%s> is not present", header.getName())
        .isTrue();
  }

  public void isAbsent() {
    assertThat(header.isAbsent())
        .withFailMessage("Header <%s> is present", header.getName())
        .isTrue();
  }

  /**
   * Asserts presence of this header and retrieves the assertion object for this header value.
   *
   * @return header value assertion
   */
  public RequestHeaderValueAssertion value() {
    isPresent();
    return new RequestHeaderValueAssertion(header);
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions on this request
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
   * requestHeaderAssertion.verify(RequestHeaderAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<RequestHeaderAssertion> consumer) {
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
  public <T> T verify(Function<RequestHeaderAssertion, T> consumer) {
    return consumer.apply(this);
  }
}
