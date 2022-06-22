package io.candydoc.plugin.model;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.AggregateFound;
import io.candydoc.ddd.bounded_context.BoundedContextFound;
import io.candydoc.ddd.core_concept.CoreConceptFound;
import io.candydoc.ddd.core_concept.NameConflictBetweenCoreConcepts;
import io.candydoc.ddd.domain_command.DomainCommandFound;
import io.candydoc.ddd.domain_event.DomainEventFound;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.shared_kernel.SharedKernelFound;
import io.candydoc.ddd.value_object.ValueObjectFound;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DomainContextDtoMapper {

  public static List<DomainContextDto> map(List<Event> domainEvents) {
    DomainEventsToDomainContextDtos visitor = new DomainEventsToDomainContextDtos();
    return visitor.map(domainEvents);
  }

  private static final class DomainEventsToDomainContextDtos implements Event.Visitor {
    private final Map<String, List<ConceptDto>> concepts = new HashMap<>();
    private final List<BoundedContextFound> boundedContextFounds = new LinkedList<>();
    private final List<SharedKernelFound> sharedKernelFounds = new LinkedList<>();
    private final List<InteractionBetweenConceptFound> returningInteractions = new LinkedList<>();

    private void addBoundedContext(String boundedContext) {
      concepts.put(boundedContext, new LinkedList<>());
    }

    private void addSharedKernel(String sharedKernel) {
      concepts.put(sharedKernel, new LinkedList<>());
    }

    private void addConcept(String domainContext, ConceptDto conceptDto) {
      concepts.computeIfAbsent(domainContext, (key) -> new LinkedList<>()).add(conceptDto);
    }

    private List<ConceptDto> conceptsInDomainContext(String domainContext) {
      return concepts.get(domainContext);
    }

    private List<ConceptDto> allConcepts() {
      return concepts.values().stream()
          .flatMap(Collection::stream)
          .collect(Collectors.toUnmodifiableList());
    }

    private Optional<ConceptDto> conceptFromClassName(String className) {
      return allConcepts().stream()
          .filter(conceptDto -> conceptDto.getCanonicalName().equals(className))
          .findFirst();
    }

    public List<DomainContextDto> map(List<Event> events) {
      events.forEach(domainEvent -> domainEvent.accept(this));
      applyInteraction();
      return Stream.of(
              boundedContextFounds.stream()
                  .map(
                      event ->
                          BoundedContextDto.builder()
                              .packageName(event.getPackageName())
                              .concepts(conceptsInDomainContext(event.getPackageName()))
                              .simpleName(event.getSimpleName())
                              .canonicalName(event.getCanonicalName())
                              .description(event.getDescription())
                              .build())
                  .collect(Collectors.toList()),
              sharedKernelFounds.stream()
                  .map(
                      event ->
                          SharedKernelDto.builder()
                              .packageName(event.getPackageName())
                              .concepts(conceptsInDomainContext(event.getPackageName()))
                              .simpleName(event.getSimpleName())
                              .canonicalName(event.getCanonicalName())
                              .description(event.getDescription())
                              .build())
                  .collect(Collectors.toList()))
          .flatMap(Collection::stream)
          .collect(Collectors.toUnmodifiableList());
    }

    public void apply(NameConflictBetweenCoreConcepts event) {
      event
          .getCoreConcepts()
          .forEach(
              conflictingClass ->
                  concepts.values().stream()
                      .flatMap(Collection::stream)
                      .filter(
                          coreConceptDto ->
                              coreConceptDto.getCanonicalName().equals(conflictingClass))
                      .forEach(
                          coreConceptDto ->
                              coreConceptDto.addError(
                                  "Share same name with another core concept")));
    }

    public void apply(ConceptRuleViolated event) {
      conceptFromClassName(event.getConceptName())
          .ifPresent(conceptDto -> conceptDto.addError(event.getReason()));
    }

    public void apply(AggregateFound event) {
      ConceptDto aggregateDto =
          ConceptDto.builder()
              .description(event.getDescription())
              .simpleName(event.getSimpleName())
              .canonicalName(event.getCanonicalName())
              .type(ConceptType.AGGREGATE)
              .build();

      addConcept(event.getDomainContext(), aggregateDto);
    }

    public void apply(BoundedContextFound event) {
      boundedContextFounds.add(event);

      addBoundedContext(event.getPackageName());
    }

    public void apply(CoreConceptFound event) {
      ConceptDto coreConceptDto =
          ConceptDto.builder()
              .simpleName(event.getSimpleName())
              .description(event.getDescription())
              .canonicalName(event.getCanonicalName())
              .type(ConceptType.CORE_CONCEPT)
              .build();

      addConcept(event.getDomainContext(), coreConceptDto);
    }

    public void apply(DomainCommandFound event) {
      ConceptDto commandDto =
          ConceptDto.builder()
              .description(event.getDescription())
              .simpleName(event.getSimpleName())
              .canonicalName(event.getCanonicalName())
              .type(ConceptType.DOMAIN_COMMAND)
              .build();

      addConcept(event.getDomainContext(), commandDto);
    }

    public void apply(DomainEventFound event) {
      ConceptDto domainEventDto =
          ConceptDto.builder()
              .description(event.getDescription())
              .simpleName(event.getSimpleName())
              .canonicalName(event.getCanonicalName())
              .type(ConceptType.DOMAIN_EVENT)
              .build();

      addConcept(event.getDomainContext(), domainEventDto);
    }

    public void apply(SharedKernelFound event) {
      sharedKernelFounds.add(event);

      addSharedKernel(event.getPackageName());
    }

    public void apply(ValueObjectFound event) {
      ConceptDto valueObjectDto =
          ConceptDto.builder()
              .description(event.getDescription())
              .simpleName(event.getSimpleName())
              .canonicalName(event.getCanonicalName())
              .type(ConceptType.VALUE_OBJECT)
              .build();

      addConcept(event.getDomainContext(), valueObjectDto);
    }

    public void apply(InteractionBetweenConceptFound event) {
      returningInteractions.add(event);
    }

    public void applyInteraction() {
      returningInteractions.forEach(
          interaction -> {
            Optional<ConceptDto> fromConceptOpt = conceptFromClassName(interaction.getFrom());
            Optional<ConceptDto> withConceptOpt = conceptFromClassName(interaction.getWith());

            if (fromConceptOpt.isPresent() && withConceptOpt.isPresent()) {
              ConceptDto fromConcept = fromConceptOpt.get();
              ConceptDto withConcept = withConceptOpt.get();

              fromConcept.addInteractsWith(
                  InteractionDto.builder()
                      .simpleName(withConcept.getSimpleName())
                      .canonicalName(withConcept.getCanonicalName())
                      .build());

              withConcept.addInteractsWith(
                  InteractionDto.builder()
                      .simpleName(fromConcept.getSimpleName())
                      .canonicalName(fromConcept.getCanonicalName())
                      .build());
            }
          });
    }
  }
}
