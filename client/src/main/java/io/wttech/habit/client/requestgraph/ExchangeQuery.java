package io.wttech.habit.client.requestgraph;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ExchangeQuery {

  private final Predicate<RequestGraphNode> filter;

  ExchangeQuery(
      Predicate<RequestGraphNode> filter) {
    this.filter = filter;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Predicate<RequestGraphNode> asPredicate() {
    return filter;
  }

  public static class Builder {

    private final List<Predicate<RequestGraphNode>> filters = Lists.newArrayList();

    public Builder protocol(String protocol) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getProtocol(), protocol));
      return this;
    }

    public Builder version(String version) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getVersion(), version));
      return this;
    }

    public Builder host(String host) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getHost(), host));
      return this;
    }

    public Builder port(int port) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getPort(), port));
      return this;
    }

    public Builder path(String path) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getPath(), path));
      return this;
    }

    public Builder fullPath(String fullPath) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getFullPath(), fullPath));
      return this;
    }

    public Builder method(String method) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getMethod(), method));
      return this;
    }

    public Builder query(String paramaeter, String value) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getQuery(paramaeter), value));
      return this;
    }

    public Builder requestBody(String body) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getBody(), body));
      return this;
    }

    public Builder requestHeader(String header, String value) {
      filters.add(FieldPredicate.of(node -> node.getRequest().getHeader(header), value));
      return this;
    }

    public Builder responseBody(String body) {
      filters.add(FieldPredicate.of(node -> node.getResponse().getBody(), body));
      return this;
    }

    public Builder responseHeader(String header, String value) {
      filters.add(FieldPredicate.of(node -> node.getResponse().getHeader(header), value));
      return this;
    }

    public Builder statusCode(int statusCode) {
      filters.add(FieldPredicate.of(node -> node.getResponse().getStatusCode(), statusCode));
      return this;
    }

    public Builder reason(String reason) {
      filters.add(FieldPredicate.of(node -> node.getResponse().getReason(), reason));
      return this;
    }

    public Builder isLeaf() {
      filters.add(FieldPredicate.of(RequestGraphNode::isLeaf, true));
      return this;
    }

    public ExchangeQuery build() {
      Predicate<RequestGraphNode> predicate = filters.stream().reduce(x -> true, Predicate::and);
      return new ExchangeQuery(predicate);
    }

  }

  private static class FieldPredicate implements Predicate<RequestGraphNode> {

    private final Function<RequestGraphNode, Object> getter;
    private final Object value;

    public FieldPredicate(
        Function<RequestGraphNode, Object> getter, Object value) {
      this.getter = getter;
      this.value = value;
    }

    public static FieldPredicate of(Function<RequestGraphNode, Object> getter, Object value) {
      return new FieldPredicate(getter, value);
    }

    @Override
    public boolean test(RequestGraphNode requestGraphNode) {
      return value.equals(getter.apply(requestGraphNode));
    }

  }

}
