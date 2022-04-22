package io.wttech.habit.client.requestgraph;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestQuery {

  private final Map<String, List<String>> query;

  public RequestQuery(Map<String, List<String>> query) {
    this.query = query;
  }

  public boolean isEmpty() {
    return query.isEmpty();
  }

  public boolean isNotEmpty() {
    return !query.isEmpty();
  }

  public int getParameterSize() {
    return query.size();
  }

  public int getValuesSize() {
    return query.values().stream()
        .mapToInt(values -> Optional.ofNullable(values).map(List::size).orElse(0))
        .sum();
  }

  public RequestQueryParameter getParameter(String parameter) {
    return new RequestQueryParameter(parameter,
        query.getOrDefault(parameter, Collections.emptyList()), query.containsKey(parameter));
  }

  public boolean containsParameter(String name) {
    return query.containsKey(name);
  }

}
