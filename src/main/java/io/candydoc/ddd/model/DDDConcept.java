package io.candydoc.ddd.model;

import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.shared_kernel.SharedKernel;
import io.candydoc.ddd.value_object.ValueObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
public abstract class DDDConcept {
  private final CanonicalName canonicalName;
  private final SimpleName simpleName;
  private final PackageName packageName;
  private final Description description;

  public abstract <T> T apply(Visitor<T> visitor);

  public interface Visitor<T> {
    T aggregate(Aggregate aggregate);

    T boundedContext(BoundedContext boundedContext);

    T coreConcept(CoreConcept coreConcept);

    T domainCommand(DomainCommand domainCommand);

    T domainEvent(DomainEvent domainEvent);

    T sharedKernel(SharedKernel sharedKernel);

    T valueObject(ValueObject valueObject);
  }
}
