package io.wttech.habit.client.request.specification;

public class QueryStringParameter {

  private final String name;
  private final String value;

  QueryStringParameter(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public static QueryStringParameter of(String name, String value) {
    return new QueryStringParameter(name, value);
  }

  public static QueryStringParameter of(String name) {
    return new QueryStringParameter(name, null);
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public String asQueryString() {
    return name + valuePartAsString();
  }

  private String valuePartAsString() {
    return value == null
        ? ""
        : "=" + value;
  }

}
