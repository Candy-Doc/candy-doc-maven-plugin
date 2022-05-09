package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when a bounded context is found in a package")
public class BoundedContextFound implements Event {
  @NonNull String simpleName;
  @NonNull String description;
  @NonNull String packageName;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
