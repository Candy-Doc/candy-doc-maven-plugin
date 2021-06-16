package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.domain.annotations.DomainEvent(description = "Emitted when a bounded context is found in a package")
public class BoundedContextFound implements DomainEvent {
    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    String packageName;

    public void accept(Visitor v) {
        v.apply(this);
    }
}
