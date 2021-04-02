package io.candydoc.domain;

import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Builder
@Value
@ToString
public class GenerateDocumentation {
    @Singular("packagesToScan")
    List<String> packagesToScan;
}
