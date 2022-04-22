package io.wttech.habit.client.requestgraph.assertion.response;

import io.wttech.habit.client.requestgraph.Response;

public class ResponseAssertion {

  private final Response response;

  public ResponseAssertion(Response response) {
    this.response = response;
  }

  public ResponseCodeAssertion code() {
    return new ResponseCodeAssertion(response.getStatusCode());
  }

  public ReasonAssertion reason() {
    return new ReasonAssertion(response.getReason());
  }

  public ResponseBodyAssertion body() {
    return new ResponseBodyAssertion(response.getBody());
  }

  public ResponseHeadersAssertion headers() {
    return new ResponseHeadersAssertion(response.getHeaders());
  }

}
