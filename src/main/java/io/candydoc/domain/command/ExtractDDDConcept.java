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
public class ExtractDDDConcept implements Command{

    public void accept(Visitor visitor) {
        visitor.handle(this);
    }

    @Singular("packagesToScan")
    List<String> packagesToScan;
}

