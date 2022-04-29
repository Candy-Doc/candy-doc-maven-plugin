package io.candydoc.ddd.core_concept;

import io.candydoc.ddd.model.DDDConcept;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class CoreConcept extends DDDConcept {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.coreConcept(this);
  }
}
