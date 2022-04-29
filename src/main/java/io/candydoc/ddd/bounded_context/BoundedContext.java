package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.model.DDDConcept;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class BoundedContext extends DDDConcept {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.boundedContext(this);
  }
}
