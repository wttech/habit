package io.wttech.habit.server.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import org.springframework.stereotype.Component;

@Component
public class DockerJavaClientProvider {

  public DockerClient get() {
    DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .build();
    NettyDockerCmdExecFactory factory = new NettyDockerCmdExecFactory();
    return DockerClientBuilder.getInstance(config)
        .withDockerCmdExecFactory(factory)
        .build();
  }

}
