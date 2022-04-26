package io.candydoc.ddd.core_concept;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when two core concept share the same name")
public class NameConflictBetweenCoreConcepts implements Event {
  @NonNull List<String> coreConcepts;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
