package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.domain.annotations.DomainEvent(description = "Emitted when a rule is violated")
public class ConceptRuleViolated implements DomainEvent{
    public void accept(Visitor v) {
        v.apply(this);
    }

    @NonNull
    String conceptFullName;
    @NonNull
    String reason;
}