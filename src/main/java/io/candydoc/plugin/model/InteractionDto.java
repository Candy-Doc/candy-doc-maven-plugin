package io.candydoc.plugin.model;

import lombok.*;

@Builder
@ToString
@Getter
@EqualsAndHashCode
public class InteractionDto {
  @NonNull String simpleName;
  @NonNull String canonicalName;
}
