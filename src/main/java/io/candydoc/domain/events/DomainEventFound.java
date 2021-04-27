package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class DomainEventFound implements DomainEvent{
    @NonNull
    String description;
    @NonNull
    String className;
    @NonNull
    String boundedContext;

    public void accept(Visitor visitor) {
        visitor.apply(this);
    }
}
