package io.candydoc.ddd.annotations;

import io.candydoc.ddd.model.DDDPattern;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

public class DDDKeywords {

  public static final Set<Class<? extends Annotation>> KEYWORDS =
      Set.of(
          BoundedContext.class,
          SharedKernel.class,
          Aggregate.class,
          DomainEvent.class,
          DomainCommand.class,
          ValueObject.class);

  public static final Map<Class<? extends Annotation>, DDDPattern> PATTERN_FROM_ANNOTATION =
    Map.of(BoundedContext.class, DDDPattern.Strategic.BOUNDED_CONTEXT,
           SharedKernel.class, DDDPattern.Strategic.SHARED_KERNEL,
           Aggregate.class, DDDPattern.Tactical.AGGREGATE,
           DomainEvent.class, DDDPattern.Tactical.DOMAIN_EVENT,
           DomainCommand.class, DDDPattern.Tactical.DOMAIN_COMMAND,
           ValueObject.class, DDDPattern.Tactical.VALUE_OBJECT);

  private DDDKeywords() {}

  public static DDDPattern patternOf(Annotation annotation) {
    return PATTERN_FROM_ANNOTATION.get(annotation.annotationType());
  }
}
