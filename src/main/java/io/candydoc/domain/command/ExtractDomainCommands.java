package io.candydoc.domain.command;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ExtractDomainCommands implements Command {
    String packageToScan;

    public void accept(Visitor visitor) {
        visitor.handle(this);
    }
}
