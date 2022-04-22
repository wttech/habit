package io.wttech.habit.client.requestgraph.assertion.request;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.RequestQueryParameter;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.ObjectAssert;

public class QueryParameterAssertion {

  private final RequestQueryParameter queryParameter;

  QueryParameterAssertion(
      RequestQueryParameter queryParameter) {
    this.queryParameter = queryParameter;
  }

  /**
   * Asserts that query string parameter has appeared in the query string.
   * <p>
   * This assertion will pass even if query parameter is used as a marker. It does not have to
   * contain any value.
   */
  public void isPresent() {
    assertThat(queryParameter.isPresent())
        .withFailMessage("Expected query parameter <%s> to be present", queryParameter.getName())
        .isTrue();
  }

  /**
   * Asserts that query string parameter has not appeared in the query string.
   */
  public void isAbsent() {
    assertThat(queryParameter.isAbsent())
        .withFailMessage("Expected query parameter <%s> to be absent", queryParameter.getName())
        .isTrue();
  }

  /**
   * <p>
   * Asserts that this parameter has appeared in the query string and has at least one value.
   * <p>
   * Assertion passes for parameter named "id" when:
   * <ul>
   * <li>?id=value1</li>
   * <li>?id=value1&amp;id=value2</li>
   * </ul>
   *
   * Assertion fails for parameter named "id" when:
   * <ul>
   * <li>query string is empty</li>
   * <li>?anotherParameter=value1</li>
   * <li>?anotherParameter=value1&amp;notIdParameter=value2</li>
   * </ul>
   */
  public void isNotEmpty() {
    isPresent();
    assertThat(queryParameter.hasValue())
        .withFailMessage("Expected query parameter <%s> to have at least one value")
        .isTrue();
  }

  /**
   * Asserts that this parameter has appeared in the query string and does not have any value.
   */
  public void isEmpty() {
    isPresent();
    assertThat(queryParameter.hasValue())
        .withFailMessage("Expected query parameter <%s> to have no values")
        .isFalse();
  }

  /**
   * Asserts that query parameter has appeared in the query string and retrieves assertion for this
   * parameter values.
   *
   * @return query parameter values assertion
   */
  public QueryParameterValuesAssertion values() {
    isPresent();
    return new QueryParameterValuesAssertion(queryParameter);
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions on this query
   * parameter.
   *
   * @return AssertJ object assertion
   */
  public ObjectAssert<RequestQueryParameter> asAssert() {
    return assertThat(queryParameter);
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
   * queryParameterAssertion.verify(QueryParameterAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<QueryParameterAssertion> consumer) {
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
  public <T> T verify(Function<QueryParameterAssertion, T> consumer) {
    return consumer.apply(this);
  }
}
