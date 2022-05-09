package io.wttech.habit.client;

import io.wttech.habit.client.sse.ProgressEvent;

public interface ProgressListener {

  ProgressListener NOOP = new ProgressListener() {
    @Override
    public void onEvent(ProgressEvent event) {
    }

    @Override
    public void onComplete() {
    }
  };

  void onEvent(ProgressEvent event);

  void onComplete();

}
