package io.wttech.habit.client.requestgraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Queue;

public class RequestGraphFactory {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  RequestGraphFactory() {
  }

  public static RequestGraphFactory instance() {
    return new RequestGraphFactory();
  }

  public RequestGraph create(GraphModel graphModel) {
    Request rootRequest = graphModel.getSubrequests().get(0).getRequest();
    Response rootResponse = graphModel.getSubrequests()
        .get(graphModel.getSubrequests().size() - 1).getResponse();
    RequestGraphNode root = RequestGraphNode.root(rootRequest, rootResponse);

    Queue<CorrespondingNodes> graphs = Lists.newLinkedList();
    graphs.add(new CorrespondingNodes(graphModel, root));

    while (!graphs.isEmpty()) {
      CorrespondingNodes nodes = graphs.poll();
      GraphModel graph = nodes.getJsonModel();
      RequestGraphNode requestGraph = nodes.getRequestGraphNode();

      for (GraphModel subgraph : graph.getSubrequests()) {
        RequestGraphNode newRoot = RequestGraphNode
            .of(subgraph.getRequest(), subgraph.getResponse(), requestGraph);
        requestGraph.addSubrequest(newRoot);
        graphs.add(new CorrespondingNodes(subgraph, newRoot));
      }
    }
    return root;
  }

  public RequestGraph create(String json) {
    try {
      GraphModel graphModel = objectMapper.readValue(json, GraphModel.class);
      return create(graphModel);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

  private class CorrespondingNodes {

    private final GraphModel jsonModel;
    private final RequestGraphNode requestGraphNode;

    private CorrespondingNodes(
        GraphModel jsonModel,
        RequestGraphNode requestGraphNode) {
      this.jsonModel = jsonModel;
      this.requestGraphNode = requestGraphNode;
    }

    public GraphModel getJsonModel() {
      return jsonModel;
    }

    public RequestGraphNode getRequestGraphNode() {
      return requestGraphNode;
    }
  }

}
