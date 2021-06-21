package io.candydoc.domain.command;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ExtractDomainEvents implements Command {
    String packageToScan;

    public void accept(Visitor visitor) {
        visitor.handle(this);
    }
}
