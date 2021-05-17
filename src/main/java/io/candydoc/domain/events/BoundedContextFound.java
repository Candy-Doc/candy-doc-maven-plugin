package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class BoundedContextFound implements DomainEvent {
    @NonNull
    String name;
    @NonNull
    String description;

    public void accept(Visitor v) {
        v.apply(this);
    }
}
