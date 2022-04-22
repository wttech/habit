package io.wttech.habit.client.requestgraph.assertion.request;

import io.wttech.habit.client.requestgraph.Request;

public class RequestAssertion {

  private final Request request;

  public RequestAssertion(Request request) {
    this.request = request;
  }

  public ProtocolAssertion protocol() {
    return new ProtocolAssertion(request.getProtocol());
  }

  public RequestVersionAssertion version() {
    return new RequestVersionAssertion(request.getVersion());
  }

  public HostAssertion host() {
    return new HostAssertion(request.getHost());
  }

  public PortAssertion port() {
    return new PortAssertion(request.getPort());
  }

  public MethodAssertion method() {
    return new MethodAssertion(request.getMethod());
  }

  /**
   * Retrieves the assertion DSL for the path of this request. Query string is not included.
   *
   * @return request path assertion
   */
  public PathAssertion path() {
    return new PathAssertion(request.getPath());
  }

  /**
   * Retrieves the assertion DSL for the path of this request with query string included.
   *
   * @return full request path assertion
   */
  public FullPathAssertion fullPath() {
    return new FullPathAssertion(request.getFullPath());
  }

  /**
   * Retrieves the assertion DSL for the header section of this request.
   *
   * @return headers assertion
   */
  public RequestHeadersAssertion headers() {
    return new RequestHeadersAssertion(request.getHeaders());
  }

  public RequestBodyAssertion body() {
    return new RequestBodyAssertion(request.getBody());
  }

  public QueryAssertion query() {
    return new QueryAssertion(request.getQuery());
  }

}
