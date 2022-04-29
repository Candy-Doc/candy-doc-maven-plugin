package io.candydoc.ddd.value_object;

import io.candydoc.ddd.model.DDDConcept;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ValueObject extends DDDConcept {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.valueObject(this);
  }
}
