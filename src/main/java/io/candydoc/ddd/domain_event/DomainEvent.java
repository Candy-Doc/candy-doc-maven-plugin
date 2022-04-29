package io.candydoc.ddd.domain_event;

import io.candydoc.ddd.model.DDDConcept;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class DomainEvent extends DDDConcept {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.domainEvent(this);
  }
}
