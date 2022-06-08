package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.model.DDDConcept;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class SharedKernel extends DDDConcept {

  // Todo : Replace String DDDConcept because it is relation with DDDConcept not concept name
  @NonNull private Set<String> relations;

  @Override
  public <T> T apply(Visitor<T> visitor) {
    return visitor.sharedKernel(this);
  }
}
