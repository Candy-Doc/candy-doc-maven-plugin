package io.candydoc.domain.command;

import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

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
