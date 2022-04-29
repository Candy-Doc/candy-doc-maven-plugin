package io.candydoc.ddd.value_object;

import io.candydoc.ddd.Command;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ExtractValueObjects implements Command {
  @NonNull String packageToScan;

  public void accept(Visitor visitor) {
    visitor.handle(this);
  }
}
