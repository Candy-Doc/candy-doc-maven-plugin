package io.candydoc.infra.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
@EqualsAndHashCode
public class ConceptDto {
    @NonNull
    String description;
    @NonNull
    String className;
    @NonNull
    String fullName;
}
