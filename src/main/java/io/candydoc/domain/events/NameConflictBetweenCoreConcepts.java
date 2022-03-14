package io.candydoc.domain.events;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.domain.annotations.DomainEvent(
    description = "Emitted when two core concept share the same name")
public class NameConflictBetweenCoreConcepts implements DomainEvent {
  @NonNull List<String> coreConceptClassNames;

  public void accept(Visitor v) {
    v.apply(this);
  }
}
