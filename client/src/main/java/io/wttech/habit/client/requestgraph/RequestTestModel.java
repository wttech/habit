package io.wttech.habit.client.requestgraph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

/**
 * As it is received from /requests endpoint.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestTestModel {

  private String id;
  private String environmentId;
  private Instant start;
  private Instant end;
  private GraphModel graph;

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

  public GraphModel getGraph() {
    return graph;
  }
}
