package io.candydoc.infra.model;

import lombok.*;

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

    public void addInteractsWith(String interaction) {
        interactsWith.add(interaction);
    }
}
