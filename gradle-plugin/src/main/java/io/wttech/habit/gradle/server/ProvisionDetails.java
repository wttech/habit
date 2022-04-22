package io.wttech.habit.gradle.server;

import io.wttech.habit.client.ClientFacade;

public class ProvisionDetails {

  private final String serverImage;
  private final int httpPort;

  ProvisionDetails(String serverImage, int httpPort) {
    this.serverImage = serverImage;
    this.httpPort = httpPort;
  }

  public static Builder builder() {
    return new Builder();
  }

  public int getHttpPort() {
    return httpPort;
  }

  public String getServerImage() {
    return serverImage;
  }

  public static final class Builder {

    private int httpPort = ClientFacade.DEFAULT_HTTP_PORT;
    private String serverImage;

    private Builder() {
    }

    public Builder withHttpPort(int httpPort) {
      this.httpPort = httpPort;
      return this;
    }

    public Builder withServerImage(String serverImage) {
      this.serverImage = serverImage;
      return this;
    }

    public ProvisionDetails build() {
      return new ProvisionDetails(serverImage, httpPort);
    }
  }
}
