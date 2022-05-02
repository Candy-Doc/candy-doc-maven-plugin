package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.bounded_context.BoundedContext;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SharedKernel extends BoundedContext {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.sharedKernel(this);
  }
}
