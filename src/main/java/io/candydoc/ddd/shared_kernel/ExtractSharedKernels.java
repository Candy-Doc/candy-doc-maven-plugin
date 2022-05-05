package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.Command;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ExtractSharedKernels implements Command {
  @NonNull String packageToScan;

  public void accept(Visitor visitor) {
    visitor.handle(this);
  }
}
