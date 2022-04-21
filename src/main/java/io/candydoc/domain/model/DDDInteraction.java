package io.candydoc.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DDDInteraction {
  private String name;
  private String canonicalName;
  private Class<?> annotation;
}
