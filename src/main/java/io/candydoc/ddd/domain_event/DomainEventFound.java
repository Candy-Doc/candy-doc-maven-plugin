package io.candydoc.ddd.domain_event;

import io.candydoc.ddd.Event;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.ddd.annotations.DomainEvent(
    description = "Emitted when a Domain event is found in a bounded context")
public class DomainEventFound implements Event {
  @NonNull String description;
  @NonNull String name;
  @NonNull String className;
  @NonNull String packageName;
  @NonNull String boundedContext;

  public void accept(Visitor visitor) {
    visitor.apply(this);
  }
}
