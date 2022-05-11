package io.candydoc.plugin.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class BoundedContextDto {

  @NonNull String simpleName;
  @NonNull String canonicalName;
  @NonNull String description;
  @NonNull String packageName;
  @JsonIgnore @Builder.Default List<ConceptDto> concepts = List.of();

  @JsonIgnore
  public List<ConceptType> getConceptTypes() {
    return List.of(ConceptType.values());
  }

  public List<ConceptDto> getConcepts(ConceptType key) {
    return concepts.stream()
        .filter(conceptDto -> conceptDto.getType().equals(key))
        .collect(Collectors.toUnmodifiableList());
  }

  @JsonGetter
  public List<ConceptDto> getAggregates() {
    return getConcepts(ConceptType.AGGREGATE);
  }

  @JsonGetter
  public List<ConceptDto> getCoreConcepts() {
    return getConcepts(ConceptType.CORE_CONCEPT);
  }

  @JsonGetter
  public List<ConceptDto> getDomainEvents() {
    return getConcepts(ConceptType.DOMAIN_EVENT);
  }

  @JsonGetter
  public List<ConceptDto> getDomainCommands() {
    return getConcepts(ConceptType.DOMAIN_COMMAND);
  }

  @JsonGetter
  public List<ConceptDto> getValueObjects() {
    return getConcepts(ConceptType.VALUE_OBJECT);
  }
}
