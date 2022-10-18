package io.candydoc.ddd.extract_domain_models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class ExtractDomainModels {
    @Singular("packageToScan")
    private final List<String> packagesToScan;
}
