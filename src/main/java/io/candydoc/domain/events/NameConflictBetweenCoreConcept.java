package io.candydoc.domain.events;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.domain.annotations.DomainEvent(description = "Emitted when two core concept share the same name")
public class NameConflictBetweenCoreConcept implements DomainEvent {

    public void accept(Visitor v) {
        v.apply(this);
    }

    List<String> conflictingCoreConcepts;
    String UsageError;
}
