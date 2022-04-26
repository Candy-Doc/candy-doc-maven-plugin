package io.candydoc.plugin.model;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
@EqualsAndHashCode
public class ConceptDto {

  @NonNull String description;
  @NonNull String simpleName;
  @NonNull String canonicalName;
  @NonNull ConceptType type;
  @Builder.Default Set<InteractionDto> interactsWith = new LinkedHashSet<>();
  @Builder.Default List<String> errors = new LinkedList<>();

  public void addInteractsWith(InteractionDto interaction) {
    interactsWith.add(interaction);
  }

  public void addError(String error) {
    errors.add(error);
  }
}
