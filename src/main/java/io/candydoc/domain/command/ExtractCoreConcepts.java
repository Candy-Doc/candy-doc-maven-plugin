package io.candydoc.domain.command;

import io.candydoc.domain.events.DomainEvent;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ExtractCoreConcepts implements Command {
    String packageToScan;

    public void accept(Visitor visitor) {
        visitor.handle(this);
    }
}
