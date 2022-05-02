package io.candydoc.ddd.annotations;

import java.lang.annotation.Annotation;
import java.util.Set;

public class DDDKeywords {

  public static final Set<Class<? extends Annotation>> KEYWORDS =
      Set.of(
          Aggregate.class,
          BoundedContext.class,
          CoreConcept.class,
          DomainEvent.class,
          DomainCommand.class,
          SharedKernel.class,
          ValueObject.class);

  private DDDKeywords() {}
}
