package io.wttech.habit.client.requestgraph.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.RequestGraph;
import io.wttech.habit.client.requestgraph.assertion.request.RequestAssertion;
import io.wttech.habit.client.requestgraph.assertion.response.ResponseAssertion;

/**
 * Entrypoint to assertions for request and response.
 *
 * DSL for accessing all request/response elements and verifying their values.
 */
public class ExchangeAssertion {

  private final RequestGraph graph;

  public ExchangeAssertion(RequestGraph graph) {
    this.graph = graph;
  }

  public RequestAssertion request() {
    return new RequestAssertion(graph.getRequest());
  }

  public ResponseAssertion response() {
    isSuccess();
    return new ResponseAssertion(graph.getResponse());
  }

  public ErrorAssertion error() {
    isError();
    return new ErrorAssertion(graph.getError());
  }

  public void isSuccess() {
    assertThat(graph.isSuccess()).as("Is exchange successful?").isTrue();
  }

  public void isError() {
    assertThat(graph.isError()).as("Is exchange erroneous?").isTrue();
  }

}
