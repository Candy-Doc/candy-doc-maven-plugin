package io.candydoc.infra;

import io.candydoc.domain.events.*;
import io.candydoc.infra.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class BoundedContextDtoMapper {

    public static List<BoundedContextDto> map(List<DomainEvent> domainEvents) {
        DomainEventsToBoundedContextDtos visitor = new DomainEventsToBoundedContextDtos();
        return visitor.map(domainEvents);
    }

    private static final class DomainEventsToBoundedContextDtos implements DomainEvent.Visitor {
        private final Map<String, List<ConceptDto>> concepts = new HashMap<>();
        private final List<BoundedContextFound> boundedContextFounds = new LinkedList<>();
        private final List<InteractionBetweenConceptFound> returningInteractions = new LinkedList<>();

        private void addBoundedContext(String boundedContext) {
            concepts.put(boundedContext, new LinkedList<>());
        }

        private void addConcept(String boundedContext, ConceptDto conceptDto) {
            concepts.computeIfAbsent(boundedContext, (key) -> new LinkedList<>())
                .add(conceptDto);
        }

        private List<ConceptDto> conceptsInBoundedContext(String boundedContext) {
            return concepts.get(boundedContext);
        }

        private List<ConceptDto> allConcepts() {
            return concepts.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
        }

        private Optional<ConceptDto> conceptFromFullName(String fullName) {
            return allConcepts().stream()
                .filter(conceptDto -> conceptDto.getFullName().equals(fullName))
                .findFirst();
        }

        public List<BoundedContextDto> map(List<DomainEvent> events) {
            events.forEach(domainEvent -> domainEvent.accept(this));
            applyInteraction();
            return boundedContextFounds.stream()
                .map(event -> BoundedContextDto.builder()
                    .packageName(event.getPackageName())
                    .concepts(conceptsInBoundedContext(event.getPackageName()))
                    .name(event.getName())
                    .description(event.getDescription())
                    .build())
                .collect(Collectors.toUnmodifiableList());
        }

        public void apply(NameConflictBetweenCoreConcepts event) {
            event.getCoreConceptClassNames()
                .forEach(conflictingClass -> concepts.values().stream()
                    .flatMap(Collection::stream)
                    .filter(coreConceptDto -> coreConceptDto.getFullName().equals(conflictingClass))
                    .forEach(coreConceptDto -> coreConceptDto.addError("Share same name with another core concept")));
        }

        public void apply(ConceptRuleViolated event) {
            conceptFromFullName(event.getConceptFullName())
                .ifPresent(conceptDto -> conceptDto.addError(event.getReason()));
        }

        public void apply(DomainCommandFound event) {
            ConceptDto commandDto = ConceptDto.builder()
                .description(event.getDescription())
                .name(event.getClassName())
                .fullName(event.getFullName())
                .type(ConceptType.DOMAIN_COMMAND)
                .build();

            addConcept(event.getBoundedContext(), commandDto);
        }

        public void apply(BoundedContextFound event) {
            boundedContextFounds.add(event);

            addBoundedContext(event.getPackageName());
        }

        public void apply(CoreConceptFound event) {
            ConceptDto coreConceptDto = ConceptDto.builder()
                .name(event.getName())
                .description(event.getDescription())
                .fullName(event.getFullName())
                .type(ConceptType.CORE_CONCEPT)
                .build();

            addConcept(event.getBoundedContext(), coreConceptDto);
        }

        public void apply(DomainEventFound event) {
            ConceptDto domainEventDto = ConceptDto.builder()
                .description(event.getDescription())
                .name(event.getClassName())
                .fullName(event.getFullName())
                .type(ConceptType.DOMAIN_EVENT)
                .build();

            addConcept(event.getBoundedContext(), domainEventDto);
        }

        public void apply(AggregateFound event) {
            ConceptDto aggregateDto = ConceptDto.builder()
                .description(event.getDescription())
                .name(event.getName())
                .fullName(event.getFullName())
                .type(ConceptType.AGGREGATE)
                .build();

            addConcept(event.getBoundedContext(), aggregateDto);
        }

        public void apply(InteractionBetweenConceptFound event) {
            returningInteractions.add(event);
        }

        public void apply(ValueObjectFound event) {
            ConceptDto valueObjectDto = ConceptDto.builder()
                .description(event.getDescription())
                .name(event.getClassName())
                .fullName(event.getFullName())
                .type(ConceptType.VALUE_OBJECT)
                .build();

            addConcept(event.getBoundedContext(), valueObjectDto);
        }

        public void applyInteraction() {
            returningInteractions.forEach(interaction -> {
                Optional<ConceptDto> fromConceptOpt = conceptFromFullName(interaction.getFrom());
                Optional<ConceptDto> withConceptOpt = conceptFromFullName(interaction.getWith());

                if (fromConceptOpt.isPresent() && withConceptOpt.isPresent()) {
                    ConceptDto fromConcept = fromConceptOpt.get();
                    ConceptDto withConcept = withConceptOpt.get();

                    fromConcept.addInteractsWith(InteractionDto.builder()
                        .simpleName(withConcept.getName())
                        .fullName(withConcept.getFullName())
                        .build());

                    withConcept.addInteractsWith(InteractionDto.builder()
                        .simpleName(fromConcept.getName())
                        .fullName(fromConcept.getFullName())
                        .build());
                }
            });
        }
    }

}
