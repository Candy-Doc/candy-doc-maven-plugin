package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class ValueObjectFound implements DomainEvent {
    @NonNull
    String description;
    @NonNull
    String className;
    @NonNull
    String fullName;
    @NonNull
    String packageName;
    @NonNull
    String boundedContext;

    public void accept(Visitor visitor) {
        visitor.apply(this);
    }
}
