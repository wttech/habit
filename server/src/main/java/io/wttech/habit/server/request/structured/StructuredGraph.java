package io.wttech.habit.server.request.structured;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
@Getter
public class StructuredGraph {

  private ExchangeRequest request;
  private ExchangeResponse response;
  private List<StructuredGraph> subrequests = new ArrayList<>();

  public Optional<StructuredGraph> findNodeByExchangeId(String exchangeId) {
    Queue<StructuredGraph> queue = new LinkedList<>();
    queue.add(this);
    while (!queue.isEmpty()) {
      StructuredGraph current = queue.poll();
      if (current.request != null && current.request.getRequestId().equals(exchangeId)) {
        return Optional.of(current);
      }
      queue.addAll(current.subrequests);
    }
    return Optional.empty();
  }

}
