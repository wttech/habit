package io.wttech.habit.client.requestgraph.assertion.request;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.ExchangeHeaders;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.ObjectAssert;

public class RequestHeadersAssertion {

  private final ExchangeHeaders headers;

  public RequestHeadersAssertion(ExchangeHeaders headers) {
    this.headers = headers;
  }

  /**
   * Retrieves a header by the provided name.
   * <p>
   * It does execute any assertion.
   *
   * @param name header name
   * @return header assertion
   */
  public RequestHeaderAssertion byName(String name) {
    return new RequestHeaderAssertion(headers.get(name));
  }

  /**
   * Retrieves the "Accept" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion accept() {
    return byName("Accept");
  }

  /**
   * Retrieves the "Authorization" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion authorization() {
    return byName("Authorization");
  }

  /**
   * Retrieves the "Cache-Control" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion cacheControl() {
    return byName("Cache-Control");
  }

  /**
   * Retrieves the "Connection" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion connection() {
    return byName("Connection");
  }

  /**
   * Retrieves the "Content-Length" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion contentLength() {
    return byName("Content-Length");
  }

  /**
   * Retrieves the "Content-Type" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion contentType() {
    return byName("Content-Type");
  }

  /**
   * Retrieves the "Cookie" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion cookie() {
    return byName("Cookie");
  }

  /**
   * Retrieves the "Forwarded" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion forwarded() {
    return byName("Forwarded");
  }

  /**
   * Retrieves the "Host" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion host() {
    return byName("Host");
  }

  /**
   * Retrieves the "If-Match" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion ifMatch() {
    return byName("If-Match");
  }

  /**
   * Retrieves the "If-Modified-Since" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion ifModifiedSince() {
    return byName("If-Modified-Since");
  }

  /**
   * Retrieves the "Origin" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion origin() {
    return byName("Origin");
  }

  /**
   * Retrieves the "Pragma" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion pragma() {
    return byName("Pragma");
  }

  /**
   * Retrieves the "Proxy-Authorization" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion proxyAuthorization() {
    return byName("Proxy-Authorization");
  }

  /**
   * Retrieves the "Referer" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion referer() {
    return byName("Referer");
  }

  /**
   * Retrieves the "User-Agent" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion userAgent() {
    return byName("User-Agent");
  }

  /**
   * Retrieves the "Upgrade" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion upgrade() {
    return byName("Upgrade");
  }

  /**
   * Retrieves the "Via" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion via() {
    return byName("Via");
  }

  /**
   * Retrieves the "Upgrade-Insecure-Requests" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion upgradeInsecureRequests() {
    return byName("Upgrade-Insecure-Requests");
  }

  /**
   * Retrieves the "X-Forwarded-For" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion xForwardedFor() {
    return byName("X-Forwarded-For");
  }

  /**
   * Retrieves the "X-Forwarded-Host" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion xForwardedHost() {
    return byName("X-Forwarded-Host");
  }

  /**
   * Retrieves the "X-Forwarded-Proto" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion xForwardedProto() {
    return byName("X-Forwarded-Proto");
  }

  /**
   * Retrieves the "X-Http-Method-Override" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion xHttpMethodOverride() {
    return byName("X-Http-Method-Override");
  }

  /**
   * Retrieves the "X-Csrf-Token" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion xCsrfToken() {
    return byName("X-Csrf-Token");
  }

  /**
   * Retrieves the "X-Request-Id" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion xRequestId() {
    return byName("X-Request-Id");
  }

  /**
   * Retrieves the "X-Correlation-ID" header.
   *
   * @return header assertion
   */
  public RequestHeaderAssertion xCorrelationId() {
    return byName("X-Correlation-ID");
  }

  public ObjectAssert<ExchangeHeaders> asAssert() {
    return assertThat(headers);
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
   * requestHeadersAssertion.verify(HeadersAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<RequestHeadersAssertion> consumer) {
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
  public <T> T verify(Function<RequestHeadersAssertion, T> consumer) {
    return consumer.apply(this);
  }
}

