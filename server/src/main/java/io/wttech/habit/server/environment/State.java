package io.wttech.habit.server.environment;

import com.fasterxml.jackson.annotation.JsonValue;

public enum State {

  UP("up"), DOWN("down"), STARTING("starting"), ERROR("error");

  private final String name;

  State(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
