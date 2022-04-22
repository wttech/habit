package io.wttech.habit.junit;

public class EnvironmentUnavailableException extends RuntimeException {

  public EnvironmentUnavailableException() {
  }

  public EnvironmentUnavailableException(String message) {
    super(message);
  }

  public EnvironmentUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }

  public EnvironmentUnavailableException(Throwable cause) {
    super(cause);
  }

  public EnvironmentUnavailableException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
