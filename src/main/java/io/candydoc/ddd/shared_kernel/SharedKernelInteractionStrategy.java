package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.interaction.InteractionStrategy;
import io.candydoc.ddd.model.DDDConcept;
import io.candydoc.ddd.value_object.ValueObject;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SharedKernelInteractionStrategy implements InteractionStrategy<SharedKernel> {

  @NonNull private final DDDConceptFinder conceptFinder;

  public List<Event> checkInteractions(SharedKernel concept) {
    return conceptFinder.findDDDConcepts(concept.getPackageName()).stream()
        .filter(
            anotherConcept -> !anotherConcept.getCanonicalName().equals(concept.getCanonicalName()))
        .map(
            anotherConcept ->
                anotherConcept.apply(
                    new DDDConcept.Visitor<Event>() {
                      @Override
                      public Event aggregate(Aggregate aggregate) {
                        return ConceptRuleViolated.builder()
                            .conceptName(aggregate.getCanonicalName().value())
                            .reason("Shared kernel can not have aggregate.")
                            .build();
                      }

                      @Override
                      public Event boundedContext(BoundedContext boundedContext) {
                        return ConceptRuleViolated.builder()
                            .conceptName(boundedContext.getPackageName().value())
                            .reason(
                                "Bounded context "
                                    + boundedContext.getSimpleName().value()
                                    + " shouldn't be in a shared kernel.")
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
                        return ConceptRuleViolated.builder()
                            .conceptName(domainCommand.getCanonicalName().value())
                            .reason("Shared kernel can not have domain command.")
                            .build();
                      }

                      @Override
                      public Event domainEvent(DomainEvent domainEvent) {
                        return ConceptRuleViolated.builder()
                            .conceptName(domainEvent.getCanonicalName().value())
                            .reason("Shared kernel can not have domain event.")
                            .build();
                      }

                      @Override
                      public Event sharedKernel(SharedKernel sharedKernel) {
                        return ConceptRuleViolated.builder()
                            .conceptName(sharedKernel.getPackageName().value())
                            .reason(
                                "Shared kernel "
                                    + sharedKernel.getSimpleName().value()
                                    + " shouldn't be in another shared kernel.")
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
