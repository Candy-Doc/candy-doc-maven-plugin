package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when a shared kernel is found in a package")
public class SharedKernelFound implements Event {
  @NonNull String name;
  @NonNull String description;
  @NonNull String packageName;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
