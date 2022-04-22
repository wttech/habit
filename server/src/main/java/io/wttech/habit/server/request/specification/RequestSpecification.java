package io.wttech.habit.server.request.specification;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class RequestSpecification {

  private String method;
  private String protocol;
  private String host;
  private Integer port;
  private String path;
  private List<QueryStringParameter> queryStringParameters = new ArrayList<>();
  private Map<String, String> headers = new HashMap<>();
  private String body;

  public static Builder builder() {
    return new Builder();
  }

  public String getMethod() {
    return method;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }

  public boolean isPortPresent() {
    return port != null;
  }

  public String getPath() {
    return path;
  }

  public List<QueryStringParameter> getQueryStringParameters() {
    return queryStringParameters;
  }

  public String getQueryString() {
    String queryString = queryStringParameters.stream()
        .map(QueryStringParameter::asQueryString)
        .filter(string -> !string.isEmpty())
        .collect(Collectors.joining("&"));
    return queryString.isEmpty()
        ? ""
        : "?" + queryString;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getBody() {
    return body;
  }

  public static final class Builder {

    private String method;
    private String protocol;
    private String host;
    private Integer port;
    private String path;
    private List<QueryStringParameter> queryStringParameters = new ArrayList<>();
    private Map<String, String> headers = new HashMap<>();
    private String body;

    private Builder() {
    }

    public Builder withMethod(String method) {
      this.method = method;
      return this;
    }

    public Builder withProtocol(String protocol) {
      this.protocol = protocol;
      return this;
    }

    public Builder withHost(String host) {
      this.host = host;
      return this;
    }

    public Builder withPort(Integer port) {
      this.port = port;
      return this;
    }

    public Builder withPath(String path) {
      this.path = path;
      return this;
    }

    public Builder addQueryParameter(String parameterName) {
      this.queryStringParameters.add(QueryStringParameter.of(parameterName));
      return this;
    }

    public Builder addQueryParameter(String parameterName, String value) {
      if (value != null && !value.isEmpty()) {
        this.queryStringParameters.add(QueryStringParameter.of(parameterName, value));
      } else {
        this.queryStringParameters.add(QueryStringParameter.of(parameterName));
      }
      return this;
    }

    public Builder withQueryStringParameters(List<QueryStringParameter> queryStringParameters) {
      this.queryStringParameters = queryStringParameters;
      return this;
    }

    public Builder withHeaders(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public Builder addHeader(String name, String value) {
      this.headers.put(name, value);
      return this;
    }

    public Builder withBody(String body) {
      this.body = body;
      return this;
    }

    public RequestSpecification build() {
      RequestSpecification requestStructure = new RequestSpecification();
      requestStructure.port = this.port;
      requestStructure.body = this.body;
      requestStructure.host = this.host;
      requestStructure.method = this.method;
      requestStructure.headers = this.headers;
      requestStructure.path = this.path;
      requestStructure.queryStringParameters = this.queryStringParameters;
      requestStructure.protocol = this.protocol;
      return requestStructure;
    }
  }
}
