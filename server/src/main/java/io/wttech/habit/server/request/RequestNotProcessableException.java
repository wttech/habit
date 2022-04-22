package io.wttech.habit.server.request;

public class RequestNotProcessableException extends RuntimeException {

  public RequestNotProcessableException() {
  }

  public RequestNotProcessableException(String message) {
    super(message);
  }

  public RequestNotProcessableException(String message, Throwable cause) {
    super(message, cause);
  }

  public RequestNotProcessableException(Throwable cause) {
    super(cause);
  }

  public RequestNotProcessableException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
