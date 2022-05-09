package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
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
    return conceptFinder.findInnerBoundedContextOrSharedKernel(concept.getPackageName()).stream()
        .map(interaction -> conceptFinder.findConcept(interaction.canonicalName()))
        .map(
            anotherConcept ->
                anotherConcept.apply(
                    new DDDConcept.Visitor<Event>() {
                      @Override
                      public Event aggregate(Aggregate forbiddenConcept) {
                        return null;
                      }

                      @Override
                      public Event boundedContext(BoundedContext boundedContext) {
                        return ConceptRuleViolated.builder()
                            .conceptName(boundedContext.getPackageName().value())
                            .reason(
                                "Bounded context "
                                    + boundedContext.getSimpleName().value()
                                    + " shouldn't be in another bounded context.")
                            .build();
                      }

                      @Override
                      public Event coreConcept(CoreConcept coreConcept) {
                        return null;
                      }

                      @Override
                      public Event domainCommand(DomainCommand domainCommand) {
                        return null;
                      }

                      @Override
                      public Event domainEvent(DomainEvent domainEvent) {
                        return null;
                      }

                      @Override
                      public Event sharedKernel(SharedKernel sharedKernel) {
                        return ConceptRuleViolated.builder()
                            .conceptName(sharedKernel.getPackageName().value())
                            .reason(
                                "Shared kernel "
                                    + sharedKernel.getSimpleName().value()
                                    + " shouldn't be in a bounded context.")
                            .build();
                      }

                      @Override
                      public Event valueObject(ValueObject valueObject) {
                        return null;
                      }
                    }))
        .collect(Collectors.toUnmodifiableList());
  }
}
