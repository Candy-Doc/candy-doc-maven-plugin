package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.Command;
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
