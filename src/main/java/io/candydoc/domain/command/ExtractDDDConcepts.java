package io.candydoc.domain.command;

import io.candydoc.domain.events.DomainEvent;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Builder
@Value
@ToString
public class ExtractDDDConcepts implements Command {
    @Singular("packageToScan")
    List<String> packagesToScan;

    public void accept(Visitor visitor) {
        visitor.handle(this);
    }
}

