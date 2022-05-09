package io.wttech.habit.server.docker.container;

public final class ContainerType {

  public static final String MOCK = "mock";
  public static final String BLACK_BOX = "server";
  public static final String REQUEST_PERSISTER = "request-persister";
  public static final String FRONT_PROXY = "environment-front-proxy";
  public static final String REQUEST_GRAPH = "request-graph-server";

  private ContainerType() {
  }

}
