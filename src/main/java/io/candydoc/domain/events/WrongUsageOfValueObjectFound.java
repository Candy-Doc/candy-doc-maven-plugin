package io.candydoc.domain.events;

import io.candydoc.domain.events.DomainEvent;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class WrongUsageOfValueObjectFound implements DomainEvent {

    public void accept(Visitor v) {
        v.apply(this);
    }

    @NonNull
    String valueObject;
    String usageError;
}
