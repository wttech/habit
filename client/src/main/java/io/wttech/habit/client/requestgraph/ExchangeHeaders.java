package io.wttech.habit.client.requestgraph;

import java.util.Map;

public class ExchangeHeaders {

  private final Map<String, String> headers;

  ExchangeHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public ExchangeHeader get(String name) {
    return new ExchangeHeader(name, headers.get(name), headers.containsKey(name));
  }
}
