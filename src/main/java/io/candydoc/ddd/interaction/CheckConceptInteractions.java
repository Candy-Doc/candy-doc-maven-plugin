package io.candydoc.ddd.interaction;

import io.candydoc.ddd.Command;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class CheckConceptInteractions implements Command {
  @NonNull String className;

  public void accept(Visitor visitor) {
    visitor.handle(this);
  }
}
