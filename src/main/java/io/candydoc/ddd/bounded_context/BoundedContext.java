package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.SubdomainType;
import io.candydoc.ddd.model.DDDConcept;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class BoundedContext extends DDDConcept {

  private SubdomainType subdomainType;

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.boundedContext(this);
  }
}
