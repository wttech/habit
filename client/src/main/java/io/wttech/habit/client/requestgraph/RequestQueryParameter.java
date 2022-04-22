package io.wttech.habit.client.requestgraph;

import java.util.Collections;
import java.util.List;

public class RequestQueryParameter {

  private final String name;
  private final List<String> values;
  private final boolean present;

  RequestQueryParameter(String name, List<String> values, boolean present) {
    this.name = name;
    this.values = values;
    this.present = present;
  }

  public boolean isPresent() {
    return present;
  }

  public boolean isAbsent() {
    return !present;
  }

  public boolean hasValue() {
    return !values.isEmpty();
  }

  public boolean hasNoValue() {
    return values.isEmpty();
  }

  public String getName() {
    return name;
  }

  public List<String> getValues() {
    return Collections.unmodifiableList(values);
  }
}
