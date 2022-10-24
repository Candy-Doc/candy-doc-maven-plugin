package io.candydoc.ddd.extract_domain_vocabulary;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.SortedSet;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class ExtractDomainModels {
    @Singular("packageToScan")
    private final SortedSet<String> packagesToScan;
}
