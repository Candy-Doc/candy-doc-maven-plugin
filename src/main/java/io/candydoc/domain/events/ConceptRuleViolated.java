package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.domain.annotations.DomainEvent(description = "Emitted when a rule is violated")
public class ConceptRuleViolated implements DomainEvent {
  @NonNull String className;
  @NonNull String reason;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
