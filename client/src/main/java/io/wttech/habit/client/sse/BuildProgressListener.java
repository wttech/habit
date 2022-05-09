package io.wttech.habit.client.sse;

public interface BuildProgressListener {

  void onEvent(ProgressEvent event);

  void onError(String error);

  void onComplete();

}
