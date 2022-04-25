package io.candydoc.ddd.aggregate;

import io.candydoc.ddd.Event;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.ddd.annotations.DomainEvent(
    description = "Emitted when an aggregate is found in a bounded context")
public class AggregateFound implements Event {
  @NonNull String name;
  @NonNull String description;
  @NonNull String className;
  @NonNull String packageName;
  @NonNull String boundedContext;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
