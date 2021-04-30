package io.candydoc.infra.model;

import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@ToString
@Getter
@EqualsAndHashCode
public class CoreConceptDto {
    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    String className;
    Set<String> interactsWith;
    List<String> errors;

    public void addInteractsWith(String interaction) {
        interactsWith.add(interaction);
    }

    public void addError(String error) {errors.add(error);}
}
