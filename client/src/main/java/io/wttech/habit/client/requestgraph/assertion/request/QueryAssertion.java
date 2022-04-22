package io.wttech.habit.client.requestgraph.assertion.request;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.RequestQuery;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.ObjectAssert;

public class QueryAssertion {

  private final RequestQuery requestQuery;

  QueryAssertion(RequestQuery requestQuery) {
    this.requestQuery = requestQuery;
  }

  /**
   * Retrieves query string parameter by name.
   * <p>
   * Does not perform any assertion.
   *
   * @param name query string parameter name
   * @return query parameter assertion
   */
  public QueryParameterAssertion parameter(String name) {
    return new QueryParameterAssertion(requestQuery.getParameter(name));
  }

  /**
   * Asserts that query string does not contain any parameter.
   */
  public void isEmpty() {
    assertThat(requestQuery.isEmpty()).withFailMessage("Query string is not empty").isTrue();
  }

  /**
   * Asserts that query string contains at least one parameter.
   */
  public void isNotEmpty() {
    assertThat(requestQuery.isNotEmpty()).withFailMessage("Query string is empty").isTrue();
  }

  /**
   * Asserts the number of distinct parameters appearing in the query string.
   *
   * @param count expected number of parameters
   */
  public void isParameterCount(int count) {
    assertThat(requestQuery.getParameterSize()).as("Verifying query parameter count")
        .isEqualTo(count);
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions on this request
   * query.
   *
   * @return AssertJ object assertion
   */
  public ObjectAssert<RequestQuery> asAssert() {
    return assertThat(requestQuery);
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
   * queryAssertion.verify(QueryAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<QueryAssertion> consumer) {
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
  public <T> T verify(Function<QueryAssertion, T> consumer) {
    return consumer.apply(this);
  }
}
