package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.domain.annotations.DomainEvent(description = "Emitted when a domain command is found in a bounded context")
public class DomainCommandFound implements DomainEvent {

    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    String boundedContext;
    @NonNull
    String className;
    @NonNull
    String packageName;

    public void accept(Visitor v) {
        v.apply(this);
    }
}
