package io.candydoc.ddd.aggregate;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when an aggregate is found in a bounded context")
public class AggregateFound implements Event {
  @NonNull String simpleName;
  @NonNull String description;
  @NonNull String canonicalName;
  @NonNull String packageName;
  @NonNull String domainContext;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
