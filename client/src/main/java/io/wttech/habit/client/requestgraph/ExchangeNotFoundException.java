package io.wttech.habit.client.requestgraph;

public class ExchangeNotFoundException extends RuntimeException {

  public ExchangeNotFoundException() {
  }

  public ExchangeNotFoundException(String message) {
    super(message);
  }

  public ExchangeNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExchangeNotFoundException(Throwable cause) {
    super(cause);
  }

  public ExchangeNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
