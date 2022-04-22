package io.wttech.habit.client.requestgraph.assertion.request;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.RequestQueryParameter;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.ListAssert;

public class QueryParameterValuesAssertion {

  private final RequestQueryParameter queryParameter;

  QueryParameterValuesAssertion(
      RequestQueryParameter queryParameter) {
    this.queryParameter = queryParameter;
  }

  public void count(int count) {
    asAssert()
        .hasSize(count);
  }

  public void contain(String... values) {
    asAssert()
        .contains(values);
  }

  public void containOnly(String... values) {
    asAssert()
        .containsOnly(values);
  }

  public void doNotContain(String... values) {
    asAssert()
        .doesNotContain(values);
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions on this query
   * parameter values.
   *
   * @return AssertJ string list assertion
   */
  public ListAssert<String> asAssert() {
    return assertThat(queryParameter.getValues())
        .as("Verifying query parameter <%s> value set", queryParameter.getName());
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
   * queryParameterValuesAssertion.verify(QueryValuesAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<QueryParameterValuesAssertion> consumer) {
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
  public <T> T verify(Function<QueryParameterValuesAssertion, T> consumer) {
    return consumer.apply(this);
  }
}
