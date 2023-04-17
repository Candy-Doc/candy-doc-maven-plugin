package io.candydoc.ddd.model;

import io.candydoc.ddd.rules.DDDWarning;
import java.util.Set;
import lombok.*;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class FoundDomainObject {
  @Singular private final Set<FoundDDDPattern> matchingPatterns;
  private final PackageName packageName;
  private final CanonicalName canonicalName;
  @Singular private final Set<CanonicalName> dependencies;
  @Singular private final Set<DDDWarning> warnings;

  public FoundDDDPattern matchingPattern() {
    return matchingPatterns.iterator().next();
  }

  public boolean matchMoreThanOneDDDPattern() {
    return matchingPatterns.size() > 1;
  }
}
