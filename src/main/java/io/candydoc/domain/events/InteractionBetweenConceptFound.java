package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.domain.annotations.DomainEvent(description = "Emitted when an interaction is found between two concepts")
public class InteractionBetweenConceptFound implements DomainEvent {
    @NonNull
    String from;
    @NonNull
    String with;

    public void accept(Visitor visitor) {
        visitor.apply(this);
    }
}
