package io.candydoc.domain.events;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class DomainCommandFound implements DomainEvent{

    String description;
    String className;
    String boundedContext;

    public void accept(Visitor v) {
        v.apply(this);
    }
}
