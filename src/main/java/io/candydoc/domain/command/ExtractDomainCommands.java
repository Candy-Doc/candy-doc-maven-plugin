package io.candydoc.domain.command;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ExtractDomainCommands implements Command {
  @NonNull String packageToScan;

  public void accept(Visitor visitor) {
    visitor.handle(this);
  }
}
