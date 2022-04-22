package io.wttech.habit.server.request;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
@Indices({
    @Index(value = "environmentId", type = IndexType.NonUnique)
})
public class RequestGraph {

  @Id
  private String id;
  private String environmentId;
  private Instant start;
  private long startEpoch;
  private Instant end;
  private long endEpoch;
  private JsonNode graph;

  public static Builder builder() {
    return new Builder();
  }

  public String getId() {
    return id;
  }

  public String getEnvironmentId() {
    return environmentId;
  }

  public JsonNode getGraph() {
    return graph;
  }

  public Instant getStart() {
    return start;
  }

  public Instant getEnd() {
    return end;
  }

  public static final class Builder {

    private String id;
    private String environmentId;
    private Instant start;
    private Instant end;
    private JsonNode graph;

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

    public Builder withGraph(JsonNode graph) {
      this.graph = graph;
      return this;
    }

    public RequestGraph build() {
      RequestGraph requestGraph = new RequestGraph();
      requestGraph.graph = this.graph;
      requestGraph.environmentId = this.environmentId;
      requestGraph.start = this.start;
      requestGraph.startEpoch = this.start.toEpochMilli();
      requestGraph.end = this.end;
      requestGraph.endEpoch = this.end.toEpochMilli();
      requestGraph.id = this.id;
      return requestGraph;
    }
  }
}

