package io.candydoc.ddd.domain_event;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when a Domain event is found in a bounded context")
public class DomainEventFound implements Event {
  @NonNull String description;
  @NonNull String simpleName;
  @NonNull String canonicalName;
  @NonNull String packageName;
  @NonNull String domainContext;

  public void accept(Visitor visitor) {
    visitor.apply(this);
  }
}
