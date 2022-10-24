package io.candydoc.ddd.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class Dependency {
  private final DDDPattern pattern;
  private final CanonicalName name;
}
