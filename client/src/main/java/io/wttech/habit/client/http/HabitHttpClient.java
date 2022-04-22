package io.wttech.habit.client.http;

import io.wttech.habit.client.HabitClientException;
import io.wttech.habit.client.request.specification.RequestDefinition;
import io.wttech.habit.client.requestgraph.RequestTest;
import io.wttech.habit.client.requestgraph.RequestTestFactory;
import io.wttech.habit.client.util.JsonTranslator;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSource.Factory;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HabitHttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabitHttpClient.class);

  private static final MediaType MEDIA_TYPE_OCTET
      = MediaType.parse("application/octet-stream");

  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");

  private final String host;
  private final int port;

  private final OkHttpClient client = new OkHttpClient.Builder()
      .readTimeout(1, TimeUnit.MINUTES)
      .build();

  public HabitHttpClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public RequestTest sendTestRequest(String environmentId, RequestDefinition definition) {
    String json = JsonTranslator.instance().asString(definition);
    Request request = new Request.Builder()
        .url("http://" + host + ":" + port + "/api/v1/environments/" + environmentId + "/requests")
        .post(RequestBody.create(json, MediaType.parse("application/json")))
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (response.code() != 200) {
        throw new HabitClientException("Unexpected response from the Habit Server");
      }
      RequestTestFactory factory = RequestTestFactory.instance();
      String responseJson = response.body().string();
      return factory.create(responseJson);
    } catch (ConnectException e) {
      throw new HabitClientException("Could not connect to Habit Server on " + host + ":" + port
          + ". Make sure either server is running on localhost or HABIT_HOSTNAME environmental variable is set correctly.",
          e);
    } catch (IOException e) {
      throw new HabitClientException("Could not connect to Habit Server on " + host + ":" + port
          + ". Make sure either server is running on localhost or HABIT_HOSTNAME environmental variable is set correctly.",
          e);
    }
  }

  public void start(String id, String configuration, byte[] deploy, EventSourceListener handler) {
    LOGGER.info("Environment start request for project {}", id);
    RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("json", configuration)
        .addFormDataPart("file", "configuration.tar",
            RequestBody.create(deploy, MEDIA_TYPE_OCTET))
        .build();

    Request request = new Request.Builder()
        .url("http://" + host + ":" + port + "/api/v1/environments/" + id + "/configuration")
        .addHeader("Accept", "text/event-stream")
        .put(requestBody)
        .build();

    Factory factory = EventSources.createFactory(client);
    EventSource eventSource = factory
        .newEventSource(request, handler);

  }

  public DeployResult deploy(String id, byte[] deploy) {
    LOGGER.info("Deploy for environment {}", id);
    RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", "configuration.tar",
            RequestBody.create(deploy, MEDIA_TYPE_OCTET))
        .build();

    Request request = new Request.Builder()
        .url("http://" + host + ":" + port + "/api/v1/environments/" + id + "/configuration")
        .patch(requestBody)
        .build();

    JsonTranslator translator = JsonTranslator.instance();

    try (Response response = client.newCall(request).execute()) {
      if (response.code() == 200) {
        return DeployResult.success();
      } else {
        return translator.toClass(response.body().string(), DeployResult.class);
      }
    } catch (ConnectException e) {
      throw new HabitClientException("Could not connect to Habit Server on " + host + ":" + port
          + ". Make sure either server is running on localhost or HABIT_HOSTNAME environmental variable is set correctly.",
          e);
    } catch (IOException e) {
      throw new HabitClientException(e);
    }
  }

  public void handleUpdates(String environmentId, String buildId, EventSourceListener handler) {
    String url =
        "http://" + host + ":" + port + "/api/v1/environments/" + environmentId + "/builds/"
            + buildId;
    Factory factory = EventSources.createFactory(client);
    Request streamRequest = new Builder()
        .url(url)
        .get()
        .build();
    EventSource eventSource = factory
        .newEventSource(streamRequest, handler);
  }

  public EnvironmentShutdownResult shutdown(String id) {
    Request request = new Request.Builder()
        .url("http://" + host + ":" + port + "/api/v1/environments/" + id + "/state")
        .put(RequestBody.create("down", APPLICATION_JSON))
        .build();

    try (Response response = client.newCall(request).execute()) {
      return response.code() == 200
          ? EnvironmentShutdownResult.success()
          : EnvironmentShutdownResult.error(response.code(), response.body().string());
    } catch (ConnectException e) {
      throw new HabitClientException("Could not connect to Habit Server on " + host + ":" + port
          + ". Make sure either server is running on localhost or HABIT_HOSTNAME environmental variable is set correctly.",
          e);
    } catch (IOException e) {
      throw new HabitClientException(e);
    }
  }


}
