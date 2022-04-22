package io.wttech.habit.client.requestgraph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {

  private String protocol;
  private String version;
  private String host;
  private int port;
  private String method;
  private String path;
  private String fullPath;
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  private Map<String, String> headers;
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  private Map<String, List<String>> query;
  private String body;

  public String getProtocol() {
    return protocol;
  }

  public String getVersion() {
    return version;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public String getFullPath() {
    return fullPath;
  }

  public ExchangeHeaders getHeaders() {
    return new ExchangeHeaders(headers);
  }

  public ExchangeHeader getHeader(String name) {
    return getHeaders().get(name);
  }

  public String getBody() {
    return body;
  }

  public RequestQuery getQuery() {
    return new RequestQuery(query);
  }

  public RequestQueryParameter getQuery(String parameter) {
    return getQuery().getParameter(parameter);
  }

  public boolean hasQuery(String parameter) {
    return query.containsKey(parameter);
  }

  public boolean hasQuery() {
    return query.size() > 0;
  }

  public int getQuerySize() {
    return query.values().stream().mapToInt(List::size).sum();
  }

  public int getQuerySize(String parameter) {
    List<String> queryParameterValues = query.get(parameter);
    return queryParameterValues == null
        ? 0
        : queryParameterValues.size();
  }

}
