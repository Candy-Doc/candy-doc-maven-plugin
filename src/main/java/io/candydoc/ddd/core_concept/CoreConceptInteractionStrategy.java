package io.candydoc.ddd.core_concept;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.bounded_context.BoundedContext;
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

@RequiredArgsConstructor
public class CoreConceptInteractionStrategy implements InteractionStrategy<CoreConcept> {

  @NonNull private final DDDConceptFinder conceptFinder;

  public List<Event> checkInteractions(CoreConcept concept) {
    return conceptFinder.findInteractionsWith(concept.getCanonicalName()).stream()
        .map(interaction -> conceptFinder.findConcept(interaction.canonicalName()))
        .map(
            anotherConcept ->
                anotherConcept.apply(
                    new DDDConcept.Visitor<Event>() {
                      @Override
                      public Event aggregate(Aggregate forbiddenConcept) {
                        return ConceptRuleViolated.builder()
                            .conceptName(concept.getCanonicalName().value())
                            .reason(
                                "CoreConcept interact with Aggregates "
                                    + forbiddenConcept.getCanonicalName().value()
                                    + ".")
                            .build();
                      }

                      @Override
                      public Event boundedContext(BoundedContext boundedContext) {
                        return InteractionBetweenConceptFound.builder()
                            .from(concept.getCanonicalName().value())
                            .with(anotherConcept.getCanonicalName().value())
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
                      public Event sharedKernel(SharedKernel sharedKernel) {
                        return InteractionBetweenConceptFound.builder()
                            .from(concept.getCanonicalName().value())
                            .with(anotherConcept.getCanonicalName().value())
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
