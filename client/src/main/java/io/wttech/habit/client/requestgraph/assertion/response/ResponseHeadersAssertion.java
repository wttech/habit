package io.wttech.habit.client.requestgraph.assertion.response;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.ExchangeHeaders;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.ObjectAssert;

public class ResponseHeadersAssertion {

  private final ExchangeHeaders exchangeHeaders;

  public ResponseHeadersAssertion(ExchangeHeaders exchangeHeaders) {
    this.exchangeHeaders = exchangeHeaders;
  }

  /**
   * Retrieves a header by the provided name.
   * <p>
   * It does execute any assertion.
   *
   * @param name header name
   * @return header assertion
   */
  public ResponseHeaderAssertion byName(String name) {
    return new ResponseHeaderAssertion(exchangeHeaders.get(name));
  }

  /**
   * Retrieves the "Access-Control-Allow-Origin" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion accessControlAllowOrigin() {
    return byName("Access-Control-Allow-Origin");
  }

  /**
   * Retrieves the "Access-Control-Allow-Credentials" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion accessControlAllowCredentials() {
    return byName("Access-Control-Allow-Credentials");
  }

  /**
   * Retrieves the "Access-Control-Expose-Headers" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion accessControlExposeHeaders() {
    return byName("Access-Control-Expose-Headers");
  }

  /**
   * Retrieves the "Access-Control-Max-Age" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion accessControlMaxAge() {
    return byName("Access-Control-Max-Age");
  }

  /**
   * Retrieves the "Access-Control-Allow-Methods" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion accessControlAllowMethods() {
    return byName("Access-Control-Allow-Methods");
  }

  /**
   * Retrieves the "Access-Control-Allow-Headers" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion accessControlAllowHeaders() {
    return byName("Access-Control-Allow-Headers");
  }

  /**
   * Retrieves the "Age" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion age() {
    return byName("Age");
  }

  /**
   * Retrieves the "Allow" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion allow() {
    return byName("Allow");
  }

  /**
   * Retrieves the "Cache-Control" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion cacheControl() {
    return byName("Cache-Control");
  }

  /**
   * Retrieves the "Connection" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion connection() {
    return byName("Connection");
  }

  /**
   * Retrieves the "Content-Disposition" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion contentDisposition() {
    return byName("Content-Disposition");
  }

  /**
   * Retrieves the "Content-Encoding" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion contentEncoding() {
    return byName("Content-Encoding");
  }

  /**
   * Retrieves the "Content-Language" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion contentLanguage() {
    return byName("Content-Language");
  }

  /**
   * Retrieves the "Content-Length" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion contentLength() {
    return byName("Content-Length");
  }

  /**
   * Retrieves the "Content-Location" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion contentLocation() {
    return byName("Content-Location");
  }

  /**
   * Retrieves the "Content-Range" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion contentRange() {
    return byName("Content-Range");
  }

  /**
   * Retrieves the "Content-Security-Policy" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion contentSecurityPolicy() {
    return byName("Content-Security-Policy");
  }

  /**
   * Retrieves the "Content-Type" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion contentType() {
    return byName("Content-Type");
  }

  /**
   * Retrieves the "Date" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion date() {
    return byName("Date");
  }

  /**
   * Retrieves the "Delta-Base" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion deltaBase() {
    return byName("Delta-Base");
  }

  /**
   * Retrieves the "ETag" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion etag() {
    return byName("ETag");
  }

  /**
   * Retrieves the "Expires" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion expires() {
    return byName("Expires");
  }

  /**
   * Retrieves the "IM" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion im() {
    return byName("IM");
  }

  /**
   * Retrieves the "Last-Modified" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion lastModified() {
    return byName("Last-Modified");
  }

  /**
   * Retrieves the "Link" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion link() {
    return byName("Link");
  }

  /**
   * Retrieves the "Location" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion location() {
    return byName("Location");
  }

  /**
   * Retrieves the "Pragma" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion pragma() {
    return byName("Pragma");
  }

  /**
   * Retrieves the "Proxy-Authenticate" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion proxyAuthenticate() {
    return byName("Proxy-Authenticate");
  }

  /**
   * Retrieves the "Public-Key-Pins" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion publicKeyPins() {
    return byName("Public-Key-Pins");
  }

  /**
   * Retrieves the "Refresh" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion refresh() {
    return byName("Refresh");
  }

  /**
   * Retrieves the "Retry-After" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion retryAfter() {
    return byName("Retry-After");
  }

  /**
   * Retrieves the "Server" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion server() {
    return byName("Server");
  }

  /**
   * Retrieves the "Set-Cookie" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion setCookie() {
    return byName("Set-Cookie");
  }

  /**
   * Retrieves the "Status" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion status() {
    return byName("Status");
  }

  /**
   * Retrieves the "Strict-Transport-Security" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion strictTransportSecurity() {
    return byName("Strict-Transport-Security");
  }

  /**
   * Retrieves the "Transfer-Encoding" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion transferEncoding() {
    return byName("Transfer-Encoding");
  }

  /**
   * Retrieves the "Upgrade" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion upgrade() {
    return byName("Upgrade");
  }

  /**
   * Retrieves the "Vary" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion vary() {
    return byName("Vary");
  }

  /**
   * Retrieves the "Via" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion via() {
    return byName("Via");
  }

  /**
   * Retrieves the "X-Content-Security-Policy" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion xContentSecurityPolicy() {
    return byName("X-Content-Security-Policy");
  }

  /**
   * Retrieves the "X-Content-Type-Options" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion xContentTypeOptions() {
    return byName("X-Content-Type-Options");
  }

  /**
   * Retrieves the "X-Correlation-Id" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion xCorrelationId() {
    return byName("X-Correlation-Id");
  }

  /**
   * Retrieves the "X-Frame-Options" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion xFrameOptions() {
    return byName("X-Frame-Options");
  }

  /**
   * Retrieves the "X-Request-Id" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion xRequestId() {
    return byName("X-Request-Id");
  }

  /**
   * Retrieves the "X-XSS-Protection" header.
   *
   * @return header assertion
   */
  public ResponseHeaderAssertion xXssProtection() {
    return byName("X-XSS-Protection");
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions on response header
   * section.
   *
   * @return AssertJ object assertion
   */
  public ObjectAssert<ExchangeHeaders> asAssert() {
    return assertThat(exchangeHeaders);
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
   * headersAssertion.verify(HeadersAssertions::isValid)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<ResponseHeadersAssertion> consumer) {
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
  public <T> T verify(Function<ResponseHeadersAssertion, T> consumer) {
    return consumer.apply(this);
  }
}
