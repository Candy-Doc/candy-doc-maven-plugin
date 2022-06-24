package io.candydoc.plugin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class SharedKernelDto {

  @NonNull String simpleName;
  @NonNull String canonicalName;
  @NonNull String description;
  @NonNull String packageName;
  @NonNull Set<String> relations;
  @JsonIgnore @Builder.Default List<ConceptDto> concepts = List.of();

  public List<ConceptDto> getConcepts(ConceptType key) {
    return concepts.stream()
        .filter(conceptDto -> conceptDto.getType().equals(key))
        .collect(Collectors.toUnmodifiableList());
  }
}
