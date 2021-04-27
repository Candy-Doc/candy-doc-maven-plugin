package io.candydoc.infra.model;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@EqualsAndHashCode
public class ValueObjectDto {
    @NonNull
    String description;
    @NonNull
    String className;
}
