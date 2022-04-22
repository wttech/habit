package io.wttech.habit.client.request.specification;

import io.wttech.habit.client.http.HabitHttpClient;
import io.wttech.habit.client.request.specification.RequestDefinition.Builder;
import io.wttech.habit.client.requestgraph.RequestTest;
import io.wttech.habit.client.requestgraph.assertion.GraphAssertion;

import java.util.function.Consumer;

/**
 * <p>
 * DSL for creating and executing a test request using the POST /requests endpoint.
 *
 */
public class RequestDefinitionDSL {

  private final String environmentId;
  private final HabitHttpClient habitHttpClient;
  private final RequestDefinition.Builder definition;

  private boolean methodSet = false;
  private boolean hostSet = false;
  private boolean protocolSet = false;

  public RequestDefinitionDSL(HabitHttpClient habitHttpClient,
      String environmentId, Builder definition) {
    this.habitHttpClient = habitHttpClient;
    this.environmentId = environmentId;
    this.definition = definition;
  }

  public static RequestDefinitionDSL of(String environmentId, HabitHttpClient client) {
    return new RequestDefinitionDSL(client, environmentId, RequestDefinition.builder());
  }

  /**
   * <p>
   * Sets HTTP method of the test request. Mandatory.
   *
   * @param method "GET", "POST etc.
   * @return DSL for further customization
   */
  public RequestDefinitionDSL method(String method) {
    definition.withMethod(method.toUpperCase());
    methodSet = true;
    return this;
  }

  /**
   * Shorthand for method("GET")
   *
   * @return DSL for further customization
   * @see #method(String)
   */
  public RequestDefinitionDSL get() {
    method("GET");
    return this;
  }

  /**
   * Shorthand for method("POST")
   *
   * @return DSL for further customization
   * @see #method(String)
   */
  public RequestDefinitionDSL post() {
    method("POST");
    return this;
  }

  /**
   * Shorthand for method("PUT")
   *
   * @return DSL for further customization
   * @see #method(String)
   */
  public RequestDefinitionDSL put() {
    method("PUT");
    return this;
  }

  /**
   * Shorthand for method("PATCH")
   *
   * @return DSL for further customization
   * @see #method(String)
   */
  public RequestDefinitionDSL patch() {
    method("PATCH");
    return this;
  }

  /**
   * Shorthand for method("DELETE")
   *
   * @return DSL for further customization
   * @see #method(String)
   */
  public RequestDefinitionDSL delete() {
    method("DELETE");
    return this;
  }

  /**
   * Shorthand for method("HEAD")
   *
   * @return DSL for further customization
   * @see #method(String)
   */
  public RequestDefinitionDSL head() {
    method("HEAD");
    return this;
  }

  /**
   * Shorthand for method("OPTIONS")
   *
   * @return DSL for further customization
   * @see #method(String)
   */
  public RequestDefinitionDSL options() {
    method("OPTIONS");
    return this;
  }

  /**
   * Sets protocol of the test request. Mandatory.
   *
   * @param protocol "http" / "https"
   * @return DSL for further customization
   */
  public RequestDefinitionDSL protocol(String protocol) {
    definition.withProtocol(protocol);
    protocolSet = true;
    return this;
  }

  /**
   * Shorthand for protocol("http").
   *
   * @return DSL for further customization
   * @see #protocol(String)
   */
  public RequestDefinitionDSL http() {
    protocol("http");
    return this;
  }

  /**
   * Shorthand for protocol("https").
   *
   * @return DSL for further customization
   * @see #protocol(String)
   */
  public RequestDefinitionDSL https() {
    protocol("https");
    return this;
  }

  /**
   * <p>
   * Sets target host of the test request. Mandatory.
   * <p>
   * Host header can be set separately.
   *
   * @param host domain name or IP address, without port
   * @return DSL for further customization
   */
  public RequestDefinitionDSL host(String host) {
    definition.withHost(host);
    hostSet = true;
    return this;
  }

  /**
   * <p>
   * Sets port of the test request. Optional.
   * <p>
   * Defaults to the standard port of the selected protocol.
   * <p>
   * HTTP 80
   * <p>
   * HTTPS 443
   *
   * @param port port identifier
   * @return DSL for further customization
   */
  public RequestDefinitionDSL port(int port) {
    definition.withPort(port);
    return this;
  }

  /**
   * <p>
   * Sets path of the test request. Optional.
   * <p>
   * Defaults to an empty one.
   *
   * @param path path without query string
   * @return DSL for further customization
   */
  public RequestDefinitionDSL path(String path) {
    definition.withPath(path);
    return this;
  }

  /**
   * Adds a valueless query string parameter to the test request.
   *
   * @param parameterName query string parameter name
   * @return DSL for further customization
   */
  public RequestDefinitionDSL queryParameter(String parameterName) {
    definition.addQueryParameter(parameterName);
    return this;
  }

  /**
   * <p>
   * Adds a query string parameter with a value to the test request.
   *
   * @param parameterName query string parameter name
   * @param value query string parameter value
   * @return DSL for further customization
   */
  public RequestDefinitionDSL queryParameter(String parameterName, String value) {
    definition.addQueryParameter(parameterName, value);
    return this;
  }

  /**
   * <p>
   * Adds a query string parameter with multiple values to the test request.
   *
   * @param parameterName query string parameter name
   * @param value first query string parameter value
   * @param values additional query string parameter values
   * @return DSL for further customization
   */
  public RequestDefinitionDSL queryParameter(String parameterName, String value, String... values) {
    definition.addQueryParameter(parameterName, value);
    for (String parameterValue : values) {
      definition.addQueryParameter(parameterName, parameterValue);
    }
    return this;
  }

  /**
   * <p>
   * Defines a header together with its value on the test request.
   * <p>
   * Multiple instances of the same header are not supported.
   *
   * @param name header name
   * @param value header value
   * @return DSL for further customization
   */
  public RequestDefinitionDSL header(String name, String value) {
    definition.addHeader(name, value);
    return this;
  }

  /**
   * <p>
   * Defines body of the test request. Defaults to empty.
   * <p>
   * Is not supported for some of HTTP methods.
   *
   * @param body string representation of body
   * @return DSL for further customization
   */
  public RequestDefinitionDSL body(String body) {
    definition.withBody(body);
    return this;
  }

  /**
   * <p>
   * Test request crafting DSL extension mechanism for Java.
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
   * requestDefinitionDSL.apply(StandardRequests::setStandardValues)
   * </code>
   *
   * @param customizer consumer customizing the test request
   * @return DSL for further customization
   */
  public RequestDefinitionDSL apply(Consumer<RequestDefinitionDSL> customizer) {
    customizer.accept(this);
    return this;
  }

  /**
   * Sends the defined test request to Habit Server.
   *
   * @return request processing graph
   */
  public RequestTest send() {
    validate();
    return habitHttpClient.sendTestRequest(environmentId, definition.build());
  }

  /**
   * Shorthand for send().getGraph().assertThat()
   *
   * @return request graph assertion DSL
   */
  public GraphAssertion assertThat() {
    return send().getGraph().assertThat();
  }

  private void validate() {
    if (!methodSet) {
      throw new IllegalStateException("HTTP method has not been defined for this request.");
    }
    if (!hostSet) {
      throw new IllegalStateException("Target host has not been defined for this request.");
    }
    if (!protocolSet) {
      throw new IllegalStateException("Protocol has not been defined for this request.");
    }
  }

}
