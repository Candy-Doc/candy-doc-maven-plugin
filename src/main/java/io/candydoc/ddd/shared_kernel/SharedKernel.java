package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.model.DDDConcept;
import io.candydoc.ddd.model.Relation;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class SharedKernel extends DDDConcept {

  @NonNull private Set<Relation> relations;

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.sharedKernel(this);
  }
}
