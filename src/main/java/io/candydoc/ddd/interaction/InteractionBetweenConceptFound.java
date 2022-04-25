package io.candydoc.ddd.interaction;

import io.candydoc.ddd.Event;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.ddd.annotations.DomainEvent(
    description = "Emitted when an interaction is found between two concepts")
public class InteractionBetweenConceptFound implements Event {
  @NonNull String from;
  @NonNull String with;

  public void accept(Visitor visitor) {
    visitor.apply(this);
  }
}
