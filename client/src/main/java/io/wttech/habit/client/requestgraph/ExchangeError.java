package io.wttech.habit.client.requestgraph;

public class ExchangeError {

  private final int code;
  private final String message;

  ExchangeError(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
