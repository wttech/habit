package io.wttech.habit.client.sse;

import io.wttech.habit.client.EnvironmentBuildException;
import io.wttech.habit.client.ProgressListener;
import io.wttech.habit.client.util.JsonTranslator;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InternalEventStreamListener extends EventSourceListener {

  private final JsonTranslator jsonTranslator = JsonTranslator.instance();

  private final CountDownLatch latch;
  private final ProgressListener listener;
  private RuntimeException exception;

  public InternalEventStreamListener(CountDownLatch latch,
      ProgressListener listener) {
    this.latch = latch;
    this.listener = listener;
  }

  public boolean isSuccess() {
    return exception == null;
  }

  public boolean isError() {
    return !isSuccess();
  }

  public RuntimeException getException() {
    return exception;
  }

  @Override
  public void onClosed(@NotNull EventSource eventSource) {
    listener.onComplete();
    latch.countDown();
  }

  @Override
  public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type,
      @NotNull String data) {
    ProgressEvent progressEvent = jsonTranslator
        .toClass(data, ProgressEvent.class);
    if (progressEvent.isError()) {
      exception = new EnvironmentBuildException(progressEvent.getMessage());
      latch.countDown();
    } else {
      listener.onEvent(progressEvent);
    }
  }

  @Override
  public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t,
      @Nullable Response response) {
    try {
      exception = new EnvironmentBuildException("Event stream error occurred. " + response.body().string(), t);
    } catch (IOException e) {
      e.printStackTrace();
    }
    latch.countDown();
  }

  @Override
  public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {

  }
}
