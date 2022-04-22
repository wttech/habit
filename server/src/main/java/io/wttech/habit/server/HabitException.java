package io.wttech.habit.server;

public class HabitException extends RuntimeException {

  public HabitException() {
  }

  public HabitException(String message) {
    super(message);
  }

  public HabitException(String message, Throwable cause) {
    super(message, cause);
  }

  public HabitException(Throwable cause) {
    super(cause);
  }

  public HabitException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
