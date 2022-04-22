package io.wttech.habit.client;

public class HabitClientException extends RuntimeException {

  public HabitClientException() {
  }

  public HabitClientException(String message) {
    super(message);
  }

  public HabitClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public HabitClientException(Throwable cause) {
    super(cause);
  }

  public HabitClientException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
