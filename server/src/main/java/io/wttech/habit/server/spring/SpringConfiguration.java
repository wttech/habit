package io.wttech.habit.server.spring;

import io.wttech.habit.server.docker.DockerJavaClientProvider;
import io.wttech.habit.server.docker.SpotifyDockerClientProvider;
import io.wttech.habit.server.environment.Environment;
import io.wttech.habit.server.environment.builds.event.ProgressEvent;
import io.wttech.habit.server.request.RequestGraph;
import io.wttech.habit.server.request.logs.StructuredLogLine;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.eventbus.EventBus;
import com.spotify.docker.client.DockerClient;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@SpringBootApplication(scanBasePackages = "io.wttech.habit")
@ConfigurationPropertiesScan("io.wttech.habit")
public class SpringConfiguration {

  @Bean
  Nitrite nitrite() {
    return Nitrite.builder()
        .filePath("/opt/habit/nitrite.db")
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .openOrCreate();
  }

  @Bean
  ObjectRepository<Environment> environmentObjectRepository(Nitrite nitrite) {
    return nitrite.getRepository(Environment.class);
  }

  @Bean
  ObjectRepository<RequestGraph> graphObjectRepository(Nitrite nitrite) {
    return nitrite.getRepository(RequestGraph.class);
  }

  @Bean
  ObjectRepository<ProgressEvent> progressEventObjectRepository(Nitrite nitrite) {
    return nitrite.getRepository(ProgressEvent.class);
  }

  @Bean
  ObjectRepository<StructuredLogLine> structuredLogLineObjectRepository(Nitrite nitrite) {
    return nitrite.getRepository(StructuredLogLine.class);
  }

  @Bean
  DockerClient providesDockerClient(SpotifyDockerClientProvider dockerClientProvider) {
    return dockerClientProvider.get();
  }

  @Bean
  com.github.dockerjava.api.DockerClient provideDockerJavaClient(
      DockerJavaClientProvider provider) {
    return provider.get();
  }

  @Bean
  WebClient provideClient() {
    return WebClient.create();
  }

  @Bean
  EventBus eventBus() {
    return new EventBus();
  }

}
