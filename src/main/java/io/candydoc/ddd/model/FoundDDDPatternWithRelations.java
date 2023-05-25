package io.candydoc.ddd.model;

import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class FoundDDDPatternWithRelations extends FoundDDDPattern {
  Set<Relations> relations;
}
