package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import java.util.Set;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when a shared kernel is found in a package")
public class SharedKernelFound implements Event {
  @NonNull String simpleName;
  @NonNull String canonicalName;
  @NonNull String description;
  @NonNull String packageName;
  @NonNull Set<String> relations;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
