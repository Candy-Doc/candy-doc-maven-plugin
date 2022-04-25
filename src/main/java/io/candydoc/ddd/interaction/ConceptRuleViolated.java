package io.candydoc.ddd.interaction;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.DomainEvent;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@DomainEvent(description = "Emitted when a rule is violated")
public class ConceptRuleViolated implements Event {
  @NonNull String conceptName;
  @NonNull String reason;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
