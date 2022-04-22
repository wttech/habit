package io.wttech.habit.client.requestgraph;

public class ErrorNotAvailableException extends RuntimeException {

  public ErrorNotAvailableException() {
  }

  public ErrorNotAvailableException(String message) {
    super(message);
  }

  public ErrorNotAvailableException(String message, Throwable cause) {
    super(message, cause);
  }

  public ErrorNotAvailableException(Throwable cause) {
    super(cause);
  }

  public ErrorNotAvailableException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
