package io.wttech.habit.server.docker.container;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;

@RequiredArgsConstructor
@Slf4j
public class CommandOutputStream extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {

  private final FluxSink<byte[]> sink;

  @Override
  public void onError(Throwable throwable) {
    super.onError(throwable);
    log.error("Error when waiting for command output", throwable);
    sink.error(throwable);
  }

  @Override
  public void onComplete() {
    super.onComplete();
    log.info("Command completed");
    sink.complete();
  }

  @Override
  public void onNext(Frame frame) {
    if (frame != null) {
      switch (frame.getStreamType()) {
        case STDOUT:
        case RAW:
          sink.next(frame.getPayload());
          break;
        case STDERR:
          break;
        default:
          log.error("unknown stream type: {}", frame.getStreamType());
      }
    }
  }
}

