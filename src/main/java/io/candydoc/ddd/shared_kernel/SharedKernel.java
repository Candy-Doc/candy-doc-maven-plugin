package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.model.DomainContext;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SharedKernel extends DomainContext {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.sharedKernel(this);
  }
}
