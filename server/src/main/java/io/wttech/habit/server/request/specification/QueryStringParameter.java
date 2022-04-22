package io.wttech.habit.server.request.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Getter
public class QueryStringParameter {

  private final String name;
  private final String value;

  public static QueryStringParameter of(String name) {
    return new QueryStringParameter(name, null);
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
