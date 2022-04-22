package io.wttech.habit.client.requestgraph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * As it is received from the /requests endpoint.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphModel {

  private Request request;
  private Response response;
  private List<GraphModel> subrequests;

  public Request getRequest() {
    return request;
  }

  public Response getResponse() {
    return response;
  }

  public List<GraphModel> getSubrequests() {
    return subrequests;
  }

}
