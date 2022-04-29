package io.candydoc.ddd.core_concept;

import io.candydoc.ddd.Command;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ExtractCoreConcepts implements Command {
  @NonNull String packageToScan;

  public void accept(Visitor visitor) {
    visitor.handle(this);
  }
}
