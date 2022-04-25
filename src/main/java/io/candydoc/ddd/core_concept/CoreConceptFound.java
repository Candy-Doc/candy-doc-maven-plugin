package io.candydoc.ddd.core_concept;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when a core concept is found in a bounded context")
public class CoreConceptFound implements Event {
  @NonNull String name;
  @NonNull String description;
  @NonNull String className;
  @NonNull String packageName;
  @NonNull String boundedContext;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
