package io.wttech.habit.client;

public class EnvironmentBuildException extends RuntimeException {

  public EnvironmentBuildException() {
  }

  public EnvironmentBuildException(String message) {
    super(message);
  }

  public EnvironmentBuildException(String message, Throwable cause) {
    super(message, cause);
  }

  public EnvironmentBuildException(Throwable cause) {
    super(cause);
  }

  public EnvironmentBuildException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
