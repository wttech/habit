package io.wttech.habit.client.sse;

import io.wttech.habit.client.util.JsonTranslator;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressEventBridge extends EventSourceListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressEventBridge.class);

  private final BuildProgressListener listener;
  private final JsonTranslator jsonTranslator = JsonTranslator.instance();

  ProgressEventBridge(BuildProgressListener listener) {
    this.listener = listener;
  }

  public static ProgressEventBridge of(BuildProgressListener listener) {
    return new ProgressEventBridge(listener);
  }

  @Override
  public void onClosed(@NotNull EventSource eventSource) {
    listener.onComplete();
  }

  @Override
  public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type,
      @NotNull String data) {
    if ("error".equals(type)) {
      listener.onError(data);
    } else {
      ProgressEvent progressEvent = jsonTranslator
          .toClass(data, ProgressEvent.class);
      listener.onEvent(progressEvent);
    }
  }

  @Override
  public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t,
      @Nullable Response response) {
    listener.onError(t.getMessage());
  }

  @Override
  public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {

  }

}
