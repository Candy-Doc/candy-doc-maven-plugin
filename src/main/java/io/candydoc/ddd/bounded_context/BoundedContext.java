package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.model.DomainContext;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class BoundedContext extends DomainContext {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.boundedContext(this);
  }
}
