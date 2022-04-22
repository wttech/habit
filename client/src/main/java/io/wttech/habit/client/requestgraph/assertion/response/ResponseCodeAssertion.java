package io.wttech.habit.client.requestgraph.assertion.response;

import io.wttech.habit.client.requestgraph.assertion.IntegerAssertion;
import java.util.function.Consumer;
import java.util.function.Function;

public class ResponseCodeAssertion extends IntegerAssertion {

  ResponseCodeAssertion(int value) {
    super(value);
  }

  /**
   * Asserts that response code is between 200 and 299.
   */
  public void isSuccess() {
    asAssert().as("Is response code success").isBetween(200, 299);
  }

  /**
   * Asserts that response code is between 300 and 399.
   */
  public void isRedirection() {
    asAssert().as("Is response code redirection").isBetween(300, 399);
  }

  /**
   * Asserts that response code is between 400 and 499.
   */
  public void isClientError() {
    asAssert().as("Is response code client error").isBetween(400, 499);
  }

  /**
   * Asserts that response code is between 500 and 599.
   */
  public void isServerError() {
    asAssert().as("Is response code server error").isBetween(500, 599);
  }

  /**
   * Asserts that response code is equal to 200.
   */
  public void isOk() {
    asAssert().as("Is response code: OK").isEqualTo(200);
  }

  /**
   * Asserts that response code is equal to 201.
   */
  public void isCreated() {
    asAssert().as("Is response code: Created").isEqualTo(201);
  }

  /**
   * Asserts that response code is equal to 202.
   */
  public void isAccepted() {
    asAssert().as("Is response code: Accepted").isEqualTo(202);
  }

  /**
   * Asserts that response code is equal to 204.
   */
  public void isNoContent() {
    asAssert().as("Is response code: No Content").isEqualTo(204);
  }

  /**
   * Asserts that response code is equal to 300.
   */
  public void isMultipleChoices() {
    asAssert().as("Is response code: Multiple Choices").isEqualTo(300);
  }

  /**
   * Asserts that response code is equal to 301.
   */
  public void isMovedPermanently() {
    asAssert().as("Is response code: Moved Permanently").isEqualTo(301);
  }

  /**
   * Asserts that response code is equal to 302.
   */
  public void isFound() {
    asAssert().as("Is response code: Found").isEqualTo(302);
  }

  /**
   * Asserts that response code is equal to 303.
   */
  public void isSeeOther() {
    asAssert().as("Is response code: See Other").isEqualTo(303);
  }

  /**
   * Asserts that response code is equal to 400.
   */
  public void isBadRequest() {
    asAssert().as("Is response code: Bad Request").isEqualTo(400);
  }

  /**
   * Asserts that response code is equal to 401.
   */
  public void isUnauthorized() {
    asAssert().as("Is response code: Unauthorized").isEqualTo(401);
  }

  /**
   * Asserts that response code is equal to 402.
   */
  public void isPaymentRequired() {
    asAssert().as("Is response code: Payment Required").isEqualTo(402);
  }

  /**
   * Asserts that response code is equal to 403.
   */
  public void isForbidden() {
    asAssert().as("Is response code: Forbidden").isEqualTo(403);
  }

  /**
   * Asserts that response code is equal to 404.
   */
  public void isNotFound() {
    asAssert().as("Is response code: Not Found").isEqualTo(404);
  }

  /**
   * Asserts that response code is equal to 405.
   */
  public void isMethodNotAllowed() {
    asAssert().as("Is response code: Method Not Allowed").isEqualTo(405);
  }

  /**
   * Asserts that response code is equal to 406.
   */
  public void isNotAcceptable() {
    asAssert().as("Is response code: Not Acceptable").isEqualTo(406);
  }

  /**
   * Asserts that response code is equal to 408.
   */
  public void isRequestTimeout() {
    asAssert().as("Is response code: Request Timeout").isEqualTo(408);
  }

  /**
   * Asserts that response code is equal to 409.
   */
  public void isConflict() {
    asAssert().as("Is response code: Conflict").isEqualTo(409);
  }

  /**
   * Asserts that response code is equal to 410.
   */
  public void isGone() {
    asAssert().as("Is response code: Gone").isEqualTo(410);
  }

  /**
   * Asserts that response code is equal to 422.
   */
  public void isUnprocessableEntity() {
    asAssert().as("Is response code: Unprocessable Entity").isEqualTo(422);
  }

  /**
   * Asserts that response code is equal to 500.
   */
  public void isInternalServerError() {
    asAssert().as("Is response code: Internal Server Error").isEqualTo(500);
  }

  /**
   * Asserts that response code is equal to 501.
   */
  public void isNotImplemented() {
    asAssert().as("Is response code: Not Implemented").isEqualTo(501);
  }

  /**
   * Asserts that response code is equal to 502.
   */
  public void isBadGateway() {
    asAssert().as("Is response code: Bad Gateway").isEqualTo(502);
  }

  /**
   * Asserts that response code is equal to 503.
   */
  public void isServiceUnavailable() {
    asAssert().as("Is response code: Service Unavailable").isEqualTo(503);
  }

  /**
   * Asserts that response code is equal to 504.
   */
  public void isGatewayTimeout() {
    asAssert().as("Is response code: Gateway Timeout").isEqualTo(504);
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
   * responseCodeAssertion.verify(ResponseCodeAssertions::isCustomResponseCode)
   * </code>
   *
   * @param consumer function returning desired subgraph
   */
  public void verify(Consumer<ResponseCodeAssertion> consumer) {
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
  public <T> T verify(Function<ResponseCodeAssertion, T> consumer) {
    return consumer.apply(this);
  }
}
