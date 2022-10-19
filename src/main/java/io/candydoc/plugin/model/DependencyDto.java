package io.candydoc.plugin.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@Accessors(fluent = true)
public final class DependencyDto {
    private final String canonicalName;
    private boolean allowed;
}
