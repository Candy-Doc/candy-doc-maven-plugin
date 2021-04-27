package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class CoreConceptFound implements DomainEvent{
    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    String className;
    @NonNull
    String boundedContext;

    public void accept(Visitor v) {
        v.apply(this);
    }
}
