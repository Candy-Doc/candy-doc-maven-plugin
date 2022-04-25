package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.Event;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.ddd.annotations.DomainEvent(
    description = "Emitted when a bounded context is found in a package")
public class BoundedContextFound implements Event {
  @NonNull String name;
  @NonNull String description;
  @NonNull String packageName;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
