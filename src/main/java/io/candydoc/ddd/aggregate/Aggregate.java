package io.candydoc.ddd.aggregate;

import io.candydoc.ddd.model.DDDConcept;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Aggregate extends DDDConcept {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.aggregate(this);
  }
}
