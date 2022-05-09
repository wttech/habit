package io.wttech.habit.client.sse;

import java.util.function.Consumer;

public class LambdaProgressListener implements BuildProgressListener {

  private final Consumer<ProgressEvent> onNext;
  private final Runnable onComplete;
  private final Consumer<String> onError;

  public LambdaProgressListener(
      Consumer<ProgressEvent> onNext, Runnable onComplete,
      Consumer<String> onError) {
    this.onNext = onNext;
    this.onComplete = onComplete;
    this.onError = onError;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public void onEvent(ProgressEvent event) {
    onNext.accept(event);
  }

  @Override
  public void onComplete() {
    onComplete.run();
  }

  public void onError(String error) {
    onError.accept(error);
  }

  public static final class Builder {

    private Consumer<ProgressEvent> onNext = event -> {
    };
    private Consumer<String> onError = error -> {
    };
    private Runnable onComplete = () -> {
    };

    private Builder() {
    }

    public Builder onNext(Consumer<ProgressEvent> onNext) {
      this.onNext = onNext;
      return this;
    }

    public Builder onError(Consumer<String> onError) {
      this.onError = onError;
      return this;
    }

    public Builder onComplete(Runnable onComplete) {
      this.onComplete = onComplete;
      return this;
    }

    public LambdaProgressListener build() {
      return new LambdaProgressListener(onNext, onComplete, onError);
    }
  }
}
