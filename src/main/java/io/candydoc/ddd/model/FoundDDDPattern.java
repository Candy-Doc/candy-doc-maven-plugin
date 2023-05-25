package io.candydoc.ddd.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class FoundDDDPattern {
  private DDDPattern dddPattern;
  private SimpleName name;
  private Description description;

  public <T> T accept(DDDPattern.Visitor<T> visitor) {
    return dddPattern.accept(visitor);
  }
}
