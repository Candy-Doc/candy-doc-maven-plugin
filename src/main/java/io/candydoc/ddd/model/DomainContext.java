package io.candydoc.ddd.model;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class DomainContext extends DDDConcept {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.domainContext(this);
  }
}
