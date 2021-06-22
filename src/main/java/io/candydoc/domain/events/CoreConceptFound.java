package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.domain.annotations.DomainEvent(description = "Emitted when a core concept is found in a bounded context")
public class CoreConceptFound implements DomainEvent {
    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    String className;
    @NonNull
    String packageName;
    @NonNull
    String boundedContext;

    public void accept(Visitor v) {
        v.apply(this);
    }
}
