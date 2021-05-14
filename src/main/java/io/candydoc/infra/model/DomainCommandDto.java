package io.candydoc.infra.model;

import lombok.*;

@Builder
@ToString
@Getter
@EqualsAndHashCode
public class DomainCommandDto {
    @NonNull
    String description;
    @NonNull
    String className;
}
