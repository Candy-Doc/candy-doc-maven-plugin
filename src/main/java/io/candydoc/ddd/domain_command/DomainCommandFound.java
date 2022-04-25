package io.candydoc.ddd.domain_command;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when a domain command is found in a bounded context")
public class DomainCommandFound implements Event {

  @NonNull String name;
  @NonNull String description;
  @NonNull String boundedContext;
  @NonNull String className;
  @NonNull String packageName;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
