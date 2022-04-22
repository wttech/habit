package io.wttech.habit.client.requestgraph;

import io.wttech.habit.client.requestgraph.assertion.GraphAssertion;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class RequestGraphNode implements RequestGraph {

  private final Request request;
  private final Response response;
  private final ExchangeError error;

  private final RequestGraphNode parent;
  private final List<RequestGraphNode> subrequests;

  public RequestGraphNode(Request request,
      Response response, ExchangeError error,
      RequestGraphNode parent,
      List<RequestGraphNode> subrequests) {
    this.request = request;
    this.response = response;
    this.error = error;
    this.parent = parent;
    this.subrequests = subrequests;
  }

  public static RequestGraphNode of(Request request, Response response, RequestGraphNode parent) {
    return new RequestGraphNode(request, response, null, parent, Lists.newArrayList());
  }

  public static RequestGraphNode ofError(Request request, ExchangeError error,
      RequestGraphNode parent) {
    return new RequestGraphNode(request, null, error, parent, Lists.newArrayList());
  }

  public static RequestGraphNode root(Request request, Response response) {
    return new RequestGraphNode(request, response, null, null, Lists.newArrayList());
  }

  public static RequestGraphNode rootError(Request request, ExchangeError error) {
    return new RequestGraphNode(request, null, error, null, Lists.newArrayList());
  }

  public void addSubrequest(RequestGraphNode subrequest) {
    subrequests.add(subrequest);
  }

  @Override
  public Request getRequest() {
    return request;
  }

  @Override
  public Response getResponse() {
    return response;
  }

  @Override
  public ExchangeError getError() {
    throw new ErrorNotAvailableException();
  }

  @Override
  public boolean isSuccess() {
    return response != null;
  }

  @Override
  public boolean isError() {
    return error != null;
  }

  @Override
  public RequestGraph getFirstChild() {
    return findFirstChild()
        .orElseThrow(() -> new ExchangeNotFoundException("No exchange found."));
  }

  @Override
  public Optional<RequestGraph> findFirstChild() {
    if (isLeaf()) {
      throw new ExchangeNotFoundException("No exchange found.");
    }
    return Optional.of(subrequests.get(0));
  }

  @Override
  public RequestGraph getLastChild() {
    return findLastChild()
        .orElseThrow(() -> new ExchangeNotFoundException("No exchange found."));
  }

  @Override
  public Optional<RequestGraph> findLastChild() {
    if (isLeaf()) {
      throw new ExchangeNotFoundException("No exchange found.");
    }
    return Optional.of(subrequests.get(subrequests.size() - 1));
  }

  @Override
  public RequestGraph getChild(ExchangeQuery query) {
    return subrequests.stream()
        .filter(query.asPredicate())
        .findFirst()
        .orElseThrow(ExchangeNotFoundException::new);
  }

  @Override
  public Optional<RequestGraph> findChild(ExchangeQuery query) {
    return subrequests.stream()
        .filter(query.asPredicate())
        .map(node -> (RequestGraph) node)
        .findFirst();
  }

  @Override
  public RequestGraph getChild(int index) {
    return findChild(index)
        .orElseThrow(() -> new ExchangeNotFoundException());
  }

  @Override
  public Optional<RequestGraph> findChild(int index) {
    Preconditions.checkArgument(index >= 0, "Index must not be negative");
    if (index >= subrequests.size()) {
      return Optional.empty();
    }
    return Optional.of(subrequests.get(index));
  }

  @Override
  public List<RequestGraph> getChildren() {
    return ImmutableList.copyOf(subrequests);
  }

  @Override
  public List<RequestGraph> getChildren(ExchangeQuery query) {
    return subrequests.stream()
        .filter(query.asPredicate())
        .collect(ImmutableList.toImmutableList());
  }

  @Override
  public List<RequestGraph> findDescendants(ExchangeQuery query) {
    List<RequestGraph> result = Lists.newArrayList();
    Queue<RequestGraphNode> nodes = Lists.newLinkedList();
    nodes.addAll(subrequests);
    while (!nodes.isEmpty()) {
      RequestGraphNode next = nodes.poll();
      if (query.asPredicate().test(next)) {
        result.add(next);
      }
      nodes.addAll(next.subrequests);
    }
    return result;
  }

  @Override
  public RequestGraph getFirstDescendant(ExchangeQuery query) {
    return findFirstDescendant(query)
        .orElseThrow(() -> new ExchangeNotFoundException());
  }

  @Override
  public Optional<RequestGraph> findFirstDescendant(ExchangeQuery query) {
    Queue<RequestGraphNode> nodes = Lists.newLinkedList();
    nodes.addAll(subrequests);
    while (!nodes.isEmpty()) {
      RequestGraphNode next = nodes.poll();
      if (query.asPredicate().test(next)) {
        return Optional.of(next);
      }
      nodes.addAll(next.subrequests);
    }
    return Optional.empty();
  }

  @Override
  public RequestGraph getParent() {
    return findParent()
        .orElseThrow(() -> new ExchangeNotFoundException("Root exchange does not have a parent"));
  }

  @Override
  public Optional<RequestGraph> findParent() {
    return isRoot()
        ? Optional.empty()
        : Optional.of(parent);
  }

  @Override
  public boolean isRoot() {
    return parent == null;
  }

  @Override
  public boolean isLeaf() {
    return subrequests.isEmpty();
  }

  @Override
  public GraphAssertion assertThat() {
    return new GraphAssertion(this);
  }
}
