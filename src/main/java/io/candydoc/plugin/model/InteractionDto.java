package io.candydoc.plugin.model;

import lombok.*;

@Builder
@ToString
@Getter
@EqualsAndHashCode
public class InteractionDto {
  @NonNull String name;
  @NonNull String className;
}
