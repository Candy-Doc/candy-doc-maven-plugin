package io.candydoc.domain.model;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class DDDConcept {
  private final String packageName;
  private final String canonicalName;
  private final String name;
  private final String description;
  private final Class<?> parent;
  private final DDDAnnotation dddAnnotation;
  private final Set<DDDField> fields;
  private final Set<DDDMethod> methods;
}
