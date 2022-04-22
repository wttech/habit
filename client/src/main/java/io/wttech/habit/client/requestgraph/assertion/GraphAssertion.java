package io.wttech.habit.client.requestgraph.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.wttech.habit.client.requestgraph.RequestGraph;
import java.util.Optional;
import java.util.function.Function;

public class GraphAssertion {

  private final RequestGraph graph;

  public GraphAssertion(RequestGraph graph) {
    this.graph = graph;
  }

  public GraphAssertion firstSubgraph() {
    isNotLeaf();
    return new GraphAssertion(graph.getFirstChild());
  }

  public GraphAssertion lastSubgraph() {
    isNotLeaf();
    return new GraphAssertion(graph.getLastChild());
  }

  public GraphAssertion subgraph(int index) {
    isNotLeaf();
    Optional<GraphAssertion> optionalSubgraph = graph.findChild(index)
        .map(GraphAssertion::new);

    if (!optionalSubgraph.isPresent()) {
      fail("There is no subgraph of index %s", index);
    }

    return optionalSubgraph.get();
  }

  public void isRoot() {
    assertThat(graph.isRoot())
        .as("Is graph root")
        .isTrue();
  }

  public void isNotRoot() {
    assertThat(graph.isRoot())
        .as("Is graph not root")
        .isFalse();
  }

  public void isLeaf() {
    assertThat(graph.isLeaf())
        .as("Is graph leaf")
        .isTrue();
  }

  public void isNotLeaf() {
    assertThat(graph.isLeaf())
        .as("Is graph not leaf")
        .isFalse();
  }

  public ExchangeAssertion exchange() {
    return new ExchangeAssertion(graph);
  }

  /**
   * Graph traversal DSL extension mechanism for Java.
   *
   * <p>
   * While Java does not support extension methods like Kotlin it is still possible to provide a
   * somewhat similar syntax for graph traversal.
   *
   * <p>
   * Example:
   * <p>
   * <code>
   * graphAssertion.traverse(GraphTraversals::toBackDomainSubgraph)
   * </code>
   *
   * @param consumer function returning desired subgraph
   * @return graph assertion
   */
  public GraphAssertion traverse(Function<GraphAssertion, GraphAssertion> consumer) {
    return consumer.apply(this);
  }

}
