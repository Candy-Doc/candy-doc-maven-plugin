package io.candydoc.ddd.domain_event;

import io.candydoc.ddd.Command;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ExtractDomainEvents implements Command {
  @NonNull String packageToScan;

  public void accept(Visitor visitor) {
    visitor.handle(this);
  }
}
