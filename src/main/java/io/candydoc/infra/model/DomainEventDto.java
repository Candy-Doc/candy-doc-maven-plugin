package io.candydoc.infra.model;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@EqualsAndHashCode
public class DomainEventDto {
    @NonNull
    String description;
    @NonNull
    String className;
}
