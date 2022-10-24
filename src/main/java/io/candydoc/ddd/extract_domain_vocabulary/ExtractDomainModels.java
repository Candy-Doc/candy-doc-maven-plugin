package io.candydoc.ddd.extract_domain_vocabulary;

import java.util.SortedSet;
import lombok.*;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class ExtractDomainModels {
  @Singular("packageToScan")
  private final SortedSet<String> packagesToScan;
}
