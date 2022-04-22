package io.wttech.habit.client.requestgraph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

  private int statusCode;
  private String reason;
  private String body;
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  private Map<String, String> headers;

  public int getStatusCode() {
    return statusCode;
  }

  /**
   * @return textual representation of the response status code
   */
  public String getReason() {
    return reason;
  }

  public String getBody() {
    return body;
  }

  public ExchangeHeaders getHeaders() {
    return new ExchangeHeaders(headers);
  }

  /**
   * @param name - header name
   * @return header value or null
   */
  public ExchangeHeader getHeader(String name) {
    return getHeaders().get(name);
  }

  /**
   * @param name - header name
   * @return is header present
   */
  public boolean hasHeader(String name) {
    return headers.containsKey(name);
  }

}
