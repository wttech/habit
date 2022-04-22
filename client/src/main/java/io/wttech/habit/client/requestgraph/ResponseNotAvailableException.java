package io.wttech.habit.client.requestgraph;

public class ResponseNotAvailableException extends RuntimeException {

  public ResponseNotAvailableException() {
  }

  public ResponseNotAvailableException(String message) {
    super(message);
  }

  public ResponseNotAvailableException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResponseNotAvailableException(Throwable cause) {
    super(cause);
  }

  public ResponseNotAvailableException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
