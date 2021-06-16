package io.candydoc.infra;

import io.candydoc.domain.events.*;
import io.candydoc.infra.model.*;

import java.awt.desktop.OpenFilesEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BoundedContextDtoMapper {

    private static final class DtoVisitor implements DomainEvent.Visitor {

        Map<String, List<ConceptDto>> conceptMap = new HashMap<>();
        List<BoundedContextFound> boundedContextFounds = new LinkedList<>();
        List<InteractionBetweenConceptFound> returningInteractions = new LinkedList<>();

        private Map<BoundedContextDto.ConceptType, List<ConceptDto>> ConceptMapper(List<ConceptDto> concepts) {
            return concepts.stream()
                    .collect(Collectors.groupingBy(ConceptDto::getConceptType));
        }

        public List<BoundedContextDto> apply(List<DomainEvent> events) {
            events.forEach(domainEvent -> domainEvent.accept(this));
            applyInteraction();
            return boundedContextFounds.stream().map(context -> BoundedContextDto.builder()
                    .packageName(context.getPackageName())
                    .conceptsMap(ConceptMapper(conceptMap.get(context.getPackageName())))
                    .name(context.getName())
                    .description(context.getDescription())
                    .errors(new LinkedList<>())
                    .build())
                    .collect(Collectors.toList());
        }

        public void apply(NameConflictBetweenCoreConcept event) {
            event.getConflictingCoreConcepts()
                    .forEach(conflictingClasse -> conceptMap.values().stream()
                            .flatMap(Collection::stream)
                            .filter(coreConceptDto -> coreConceptDto.getFullName().equals(conflictingClasse))
                            .forEach(coreConceptDto -> coreConceptDto.addError(event.getUsageError())));
        }

        public void apply(ConceptRuleViolated event) {
            conceptMap.values().stream().flatMap(Collection::stream)
                    .filter(concept -> concept.getFullName().equals(event.getConceptFullName()))
                    .forEach(concept -> concept.addError(event.getReason()));
        }

        public void apply(DomainCommandFound event) {
            ConceptDto command = ConceptDto.builder()
                    .description(event.getDescription())
                    .name(event.getClassName())
                    .fullName(event.getFullName())
                    .conceptType(BoundedContextDto.ConceptType.DOMAIN_COMMAND)
                    .build();
            conceptMap.get(event.getBoundedContext()).add(command);
        }

        public void apply(BoundedContextFound event) {
            boundedContextFounds.add(event);
            conceptMap.put(event.getPackageName(), new LinkedList<>());
        }

        public void apply(CoreConceptFound event) {
            ConceptDto concept = ConceptDto.builder()
                    .name(event.getName())
                    .description(event.getDescription())
                    .fullName(event.getFullName())
                    .conceptType(BoundedContextDto.ConceptType.CORE_CONCEPT)
                    .build();
            conceptMap.get(event.getBoundedContext()).add(concept);
        }

        public void apply(DomainEventFound event) {
            ConceptDto domainEventDto = ConceptDto.builder()
                    .description(event.getDescription())
                    .name(event.getClassName())
                    .fullName(event.getFullName())
                    .conceptType(BoundedContextDto.ConceptType.DOMAIN_EVENT)
                    .build();
            conceptMap.get(event.getBoundedContext()).add(domainEventDto);
        }

        public void apply(AggregateFound event) {
            ConceptDto aggregateDto = ConceptDto.builder()
                    .description(event.getDescription())
                    .name(event.getName())
                    .fullName(event.getFullName())
                    .conceptType(BoundedContextDto.ConceptType.AGGREGATE)
                    .build();
            conceptMap.get(event.getBoundedContext()).add(aggregateDto);
        }

        public void apply(InteractionBetweenConceptFound event) {
            returningInteractions.add(event);
        }

        public void apply(ValueObjectFound event) {
            ConceptDto object = ConceptDto.builder()
                    .description(event.getDescription())
                    .name(event.getClassName())
                    .fullName(event.getFullName())
                    .conceptType(BoundedContextDto.ConceptType.VALUE_OBJECT)
                    .build();
            conceptMap.get(event.getBoundedContext()).add(object);
        }

        public void applyInteraction() {
            returningInteractions.forEach(interaction -> {
                List<ConceptDto> fromConcepts = conceptMap.values().stream()
                        .flatMap(Collection::stream)
                        .filter(conceptDto -> conceptDto.getFullName().equals(interaction.getFrom()))
                        .collect(Collectors.toList());
                List<ConceptDto> withConcepts = conceptMap.values().stream()
                        .flatMap(Collection::stream)
                        .filter(conceptDto -> conceptDto.getFullName().equals(interaction.getWithFullName()))
                        .collect(Collectors.toList());
                fromConcepts.forEach(fromConcept -> withConcepts.forEach(withConcept -> fromConcept.addInteractsWith(
                        InteractionDto.builder()
                                .simpleName(withConcept.getName())
                                .fullName(withConcept.getFullName())
                                .build()
                )));
                withConcepts.forEach(withConcept -> fromConcepts.forEach(fromConcept -> withConcept.addInteractsWith(
                        InteractionDto.builder()
                                .simpleName(fromConcept.getName())
                                .fullName(fromConcept.getFullName())
                                .build()
                )));
            });
        }
    }

    public List<BoundedContextDto> map(List<DomainEvent> domainEvents) {
        DtoVisitor visitor = new DtoVisitor();
        List<BoundedContextDto> boundedContextDtos = visitor.apply(domainEvents);
        return List.copyOf(boundedContextDtos);
    }
}