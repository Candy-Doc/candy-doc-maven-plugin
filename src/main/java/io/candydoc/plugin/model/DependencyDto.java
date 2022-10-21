package io.candydoc.plugin.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
public final class DependencyDto {
    private final String canonicalName;
    private boolean allowed;
}
