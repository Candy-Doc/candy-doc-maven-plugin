package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class DomainCommandFound implements DomainEvent {

    @NonNull
    String description;
    @NonNull
    String className;
    @NonNull
    String boundedContext;
    @NonNull
    String fullName;
    @NonNull
    String packageName;

    public void accept(Visitor v) {
        v.apply(this);
    }
}
