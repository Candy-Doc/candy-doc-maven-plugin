package io.candydoc.ddd.domain_command;

import io.candydoc.ddd.model.DDDConcept;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class DomainCommand extends DDDConcept {

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.domainCommand(this);
  }
}
