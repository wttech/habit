package io.wttech.habit.server.environment.requestgraph.container;

public enum MockServerVariable {

  HOSTNAME("MOCK_HOSTNAME"),
  PORTS("MOCK_PORTS");

  private final String name;

  MockServerVariable(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
