package io.candydoc.plugin.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Jacksonized
@Builder
@Getter
@Accessors(fluent = true)
public final class DomainModelDto {

    private String canonicalName;
    private String simpleName;
    private String type;
    private Set<DependencyDto> dependencies;

}
