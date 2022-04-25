package io.candydoc.ddd.interaction;

import io.candydoc.ddd.Event;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.ddd.annotations.DomainEvent(description = "Emitted when a rule is violated")
public class ConceptRuleViolated implements Event {
  @NonNull String className;
  @NonNull String reason;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
