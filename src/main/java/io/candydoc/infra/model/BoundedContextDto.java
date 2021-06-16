package io.candydoc.infra.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.candydoc.domain.annotations.Aggregate;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class BoundedContextDto {

    public enum ConceptType{
        AGGREGATE,
        CORE_CONCEPT,
        DOMAIN_COMMAND,
        DOMAIN_EVENT,
        VALUE_OBJECT;
    }

    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    String packageName;
    @JsonIgnore
    Map<ConceptType, List<ConceptDto>> conceptsMap;

    List<String> errors;

    @JsonIgnore
    public List<ConceptDto> getConcepts() {
        return conceptsMap.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public List<ConceptType> getConceptTypes() {
        return List.of(ConceptType.values());
    }

    public List<ConceptDto> getConcepts(ConceptType key) {
        conceptsMap.computeIfAbsent(key, (k)->new LinkedList<>());
        return conceptsMap.get(key);
    }

    @JsonGetter
    public List<ConceptDto> getAggregates() {
        return conceptsMap.get(ConceptType.AGGREGATE);
    }

    @JsonGetter
    public List<ConceptDto> getCoreConcepts() {
        return conceptsMap.get(ConceptType.CORE_CONCEPT);
    }

    @JsonGetter
    public List<ConceptDto> getDomainEvents() {
        return conceptsMap.get(ConceptType.DOMAIN_EVENT);
    }

    @JsonGetter
    public List<ConceptDto> getDomainCommands() {
        return conceptsMap.get(ConceptType.DOMAIN_COMMAND);
    }

    @JsonGetter
    public List<ConceptDto> getValueObjects() {
        return conceptsMap.get(ConceptType.VALUE_OBJECT);
    }
}
