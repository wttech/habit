package io.wttech.habit.client.requestgraph;

import io.wttech.habit.client.requestgraph.assertion.GraphAssertion;

import java.util.List;
import java.util.Optional;

/**
 * Model of the whole request graph as received from Habit server.
 */
public interface RequestGraph {

  /**
   * Retrieves request of the exchange at the root of this graph.
   *
   * Exchange data access method.
   *
   * @return exchange request, not null
   */
  Request getRequest();

  /**
   * Retrieves response of the exchange at the root of this graph.
   *
   * Exchange data access method.
   *
   * @return exchange response
   * @throws ResponseNotAvailableException if exchange results in an error
   */
  Response getResponse();

  /**
   * Retrieves error of the exchange at the root of this graph.
   *
   * Exchange data access method.
   *
   * @return exchange error
   * @throws ErrorNotAvailableException if exchange is successful
   */
  ExchangeError getError();

  /**
   * Indicates if exchange was successful that is if the response is available.
   *
   * @return true if success<br> false if not
   */
  boolean isSuccess();

  /**
   * Indicates if exchange resulted in an error.
   *
   * @return true if error<br> false if not
   */
  boolean isError();

  /**
   * Retrieves the first child subgraph ordered by exchange request time.
   *
   * Graph traversal method.
   *
   * @return child subgraph, not null
   * @throws ExchangeNotFoundException if there are no child subgraphs
   */
  RequestGraph getFirstChild();

  /**
   * Retrieves the first child subgraph ordered by exchange request time.
   *
   * Graph traversal method.
   *
   * @return optional containing child subgraph
   */
  Optional<RequestGraph> findFirstChild();

  /**
   * Retrieves the last child subgraph ordered by exchange request time.
   *
   * Graph traversal method.
   *
   * @return child subgraph, not null
   * @throws ExchangeNotFoundException if there are no child subgraphs
   */
  RequestGraph getLastChild();

  /**
   * Retrieves the last child subgraph ordered by exchange request time.
   *
   * Graph traversal method.
   *
   * @return optional containing child subgraph
   */
  Optional<RequestGraph> findLastChild();

  /**
   * Retrieves the first child that satisfies the provided query.
   *
   * Graph traversal method.
   *
   * @param query set of exchange filters, not null
   * @return child subgraph, not null
   * @throws ExchangeNotFoundException if no exchange satisfies the query
   */
  RequestGraph getChild(ExchangeQuery query);

  /**
   * Retrieves the first child that satisfies the provided query.
   *
   * Graph traversal method.
   *
   * @param query set of exchange filters, not null
   * @return optional containing child subgraph
   */
  Optional<RequestGraph> findChild(ExchangeQuery query);

  /**
   * Retrieves child subgraph based on index.
   *
   * Graph traversal method.
   *
   * @param index 0 based index
   * @return child subgraph, not null
   * @throws ExchangeNotFoundException if index is bigger or equal to number of children exchanges
   */
  RequestGraph getChild(int index);

  /**
   * Retrieves child subgraph based on index.
   *
   * Graph traversal method.
   *
   * @param index 0 based index
   * @return optional containing child subgraph
   */
  Optional<RequestGraph> findChild(int index);

  /**
   * Retrieves all child subgraphs.
   *
   * Graph traversal method.
   *
   * @return list of child exchanges
   */
  List<RequestGraph> getChildren();

  /**
   * Retrieves all child subgraphs that satisfy the query.
   *
   * Graph traversal method.
   *
   * @param query set of exchange filters, not null
   * @return list of child exchanges satisfying the provided query
   */
  List<RequestGraph> getChildren(ExchangeQuery query);

  /**
   * Retrieves all descendant subgraphs that satisfy the query.
   *
   * Graph traversal method.
   *
   * @param query set of exchange filters, not null
   * @return list of descendant exchanges satisfying the provided query
   */
  List<RequestGraph> findDescendants(ExchangeQuery query);

  /**
   * Retrieves the first subgraph using breadth-first search that satisfies the query.
   *
   * Graph traversal method.
   *
   * @param query set of exchange filters, not null
   * @return descendant subgraph, not null
   * @throws ExchangeNotFoundException if no descendant exchange satisfies the query
   */
  RequestGraph getFirstDescendant(ExchangeQuery query);

  /**
   * Retrieves the first subgraph using breadth-first search that satisfies the query.
   *
   * Graph traversal method.
   *
   * @param query set of exchange filters, not null
   * @return optional containing descendant subgraph
   */
  Optional<RequestGraph> findFirstDescendant(ExchangeQuery query);

  /**
   * Retrieves request graph with the parent exchange at its root.
   *
   * @return parent graph, not null
   * @throws ExchangeNotFoundException if there is no parent exchange
   */
  RequestGraph getParent();

  /**
   * Retrieves request graph with the parent exchange at its root.
   *
   * @return optional containing parent graph
   */
  Optional<RequestGraph> findParent();

  /**
   * @return true if there is no parent exchange<br> false if there is a parent exchange
   */
  boolean isRoot();

  /**
   * @return true if there are no child exchanges<br> false if there is at least on child exchange
   */
  boolean isLeaf();

  /**
   * Graph assertion DSL entrypoint.
   *
   * @return assertion chain
   */
  GraphAssertion assertThat();

}
