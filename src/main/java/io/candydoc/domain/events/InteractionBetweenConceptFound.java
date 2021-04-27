package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class InteractionBetweenConceptFound implements DomainEvent{
    @NonNull
    String from;
    @NonNull
    String with;

    public void accept(Visitor visitor) {
        visitor.apply(this);
    }
}
