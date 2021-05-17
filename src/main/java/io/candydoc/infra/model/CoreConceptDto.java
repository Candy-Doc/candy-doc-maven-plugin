package io.candydoc.infra.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SuperBuilder
@ToString
@Getter
@EqualsAndHashCode(callSuper = true)
@Value
public class CoreConceptDto extends ConceptDto {
    @NonNull
    String name;
    @Builder.Default
    Set<InteractionDto> interactsWith = new LinkedHashSet<>();
    List<String> errors;

    public void addInteractsWith(InteractionDto interaction) {
        interactsWith.add(interaction);
    }

    public void addError(String error) {
        errors.add(error);
    }
}
