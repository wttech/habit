package io.wttech.habit.client.requestgraph;

public class ExchangeHeader {

  private final String name;
  private final String value;
  private final boolean present;

  ExchangeHeader(String name, String value, boolean present) {
    this.name = name;
    this.value = value;
    this.present = present;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public boolean isPresent() {
    return present;
  }

  public boolean isAbsent() {
    return !present;
  }

}
