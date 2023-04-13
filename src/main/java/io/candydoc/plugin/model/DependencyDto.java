package io.candydoc.plugin.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@Setter
public final class DependencyDto {
  private final String refersTo;
  private DependencyAuthorization authorization;
}
