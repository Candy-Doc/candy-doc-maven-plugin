package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.interaction.InteractionStrategy;
import io.candydoc.ddd.model.DDDConcept;
import io.candydoc.ddd.shared_kernel.SharedKernel;
import io.candydoc.ddd.value_object.ValueObject;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BoundedContextInteractionStrategy implements InteractionStrategy<BoundedContext> {

  @NonNull private final DDDConceptFinder conceptFinder;

  public List<Event> checkInteractions(BoundedContext concept) {
    return conceptFinder.findDDDConcepts(concept.getPackageName()).stream()
        .filter(
            anotherConcept -> !anotherConcept.getCanonicalName().equals(concept.getCanonicalName()))
        .map(
            anotherConcept ->
                anotherConcept.apply(
                    new DDDConcept.Visitor<Event>() {
                      @Override
                      public Event aggregate(Aggregate aggregate) {
                        return InteractionBetweenConceptFound.builder()
                            .from(concept.getCanonicalName().value())
                            .with(anotherConcept.getCanonicalName().value())
                            .build();
                      }

                      @Override
                      public Event boundedContext(BoundedContext forbiddenBoundedContext) {
                        return ConceptRuleViolated.builder()
                            .conceptName(forbiddenBoundedContext.getPackageName().value())
                            .reason(
                                String.format(
                                    "Bounded context %s is not allowed in another bounded context.",
                                    forbiddenBoundedContext.getSimpleName().value()))
                            .build();
                      }

                      @Override
                      public Event coreConcept(CoreConcept coreConcept) {
                        return InteractionBetweenConceptFound.builder()
                            .from(concept.getCanonicalName().value())
                            .with(anotherConcept.getCanonicalName().value())
                            .build();
                      }

                      @Override
                      public Event domainCommand(DomainCommand domainCommand) {
                        return InteractionBetweenConceptFound.builder()
                            .from(concept.getCanonicalName().value())
                            .with(anotherConcept.getCanonicalName().value())
                            .build();
                      }

                      @Override
                      public Event domainEvent(DomainEvent domainEvent) {
                        return InteractionBetweenConceptFound.builder()
                            .from(concept.getCanonicalName().value())
                            .with(anotherConcept.getCanonicalName().value())
                            .build();
                      }

                      @Override
                      public Event sharedKernel(SharedKernel forbiddenSharedKernel) {
                        return ConceptRuleViolated.builder()
                            .conceptName(forbiddenSharedKernel.getPackageName().value())
                            .reason(
                                String.format(
                                    "Shared kernel %s is not allowed in a bounded context.",
                                    forbiddenSharedKernel.getSimpleName().value()))
                            .build();
                      }

                      @Override
                      public Event valueObject(ValueObject valueObject) {
                        return InteractionBetweenConceptFound.builder()
                            .from(concept.getCanonicalName().value())
                            .with(anotherConcept.getCanonicalName().value())
                            .build();
                      }
                    }))
        .collect(Collectors.toUnmodifiableList());
  }
}
