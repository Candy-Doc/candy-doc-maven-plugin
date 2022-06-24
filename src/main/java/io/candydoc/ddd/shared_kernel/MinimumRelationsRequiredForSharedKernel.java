package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when shared kernel doesn't have minimum required relations")
public class MinimumRelationsRequiredForSharedKernel implements Event {
  @NonNull String sharedKernel;

  public void accept(Event.Visitor v) {
    v.apply(this);
  }
}
