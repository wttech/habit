package io.wttech.habit.client.requestgraph;

import java.time.Instant;

public class RequestTest {

  private final String id;
  private final String environmentId;
  private final Instant start;
  private final Instant end;
  private final RequestGraph graph;

  RequestTest(String id, String environmentId, Instant start, Instant end,
      RequestGraph graph) {
    this.id = id;
    this.environmentId = environmentId;
    this.start = start;
    this.end = end;
    this.graph = graph;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getId() {
    return id;
  }

  public String getEnvironmentId() {
    return environmentId;
  }

  public Instant getStart() {
    return start;
  }

  public Instant getEnd() {
    return end;
  }

  public RequestGraph getGraph() {
    return graph;
  }

  public static final class Builder {

    private String id;
    private String environmentId;
    private Instant start;
    private Instant end;
    private RequestGraph graph;

    private Builder() {
    }

    public Builder withId(String id) {
      this.id = id;
      return this;
    }

    public Builder withEnvironmentId(String environmentId) {
      this.environmentId = environmentId;
      return this;
    }

    public Builder withStart(Instant start) {
      this.start = start;
      return this;
    }

    public Builder withEnd(Instant end) {
      this.end = end;
      return this;
    }

    public Builder withGraph(RequestGraph graph) {
      this.graph = graph;
      return this;
    }

    public RequestTest build() {
      return new RequestTest(id, environmentId, start, end, graph);
    }
  }
}
