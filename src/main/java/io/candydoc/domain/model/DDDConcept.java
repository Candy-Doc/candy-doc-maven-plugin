package io.candydoc.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DDDConcept {
  private final String packageName;
  private final String canonicalName;
  private final String name;
  private final String description;
}
