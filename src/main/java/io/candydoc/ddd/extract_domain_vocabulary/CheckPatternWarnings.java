package io.candydoc.ddd.extract_domain_vocabulary;

import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.DomainWarning;
import io.candydoc.ddd.strategic_pattern.StrategicPattern;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CheckPatternWarnings {

  private static final Map<DDDPattern, Function<Class<?>, Set<DomainWarning>>>
      DDD_WARNING_PROCESSOR =
          Map.of(
              StrategicPattern.BOUNDED_CONTEXT,
              CheckPatternWarnings::checkBoundedContextWarnings,
              StrategicPattern.SHARED_KERNEL,
              CheckPatternWarnings::checkSharedKernelWarnings,
              TacticalPattern.AGGREGATE,
              CheckPatternWarnings::checkAggregateWarnings,
              TacticalPattern.DOMAIN_COMMAND,
              CheckPatternWarnings::checkDomainCommandWarnings,
              TacticalPattern.DOMAIN_EVENT,
              CheckPatternWarnings::checkDomainEventWarnings,
              TacticalPattern.VALUE_OBJECT,
              CheckPatternWarnings::checkValueObjectWarnings,
              TacticalPattern.DOMAIN_ENTITY,
              CheckPatternWarnings::checkEntityWarnings,
              TacticalPattern.FACTORY,
              CheckPatternWarnings::checkFactoryWarnings,
              TacticalPattern.REPOSITORY,
              CheckPatternWarnings::checkRepositoryWarnings,
              TacticalPattern.DOMAIN_SERVICE,
              CheckPatternWarnings::checkDomainServiceWarnings);

  public static Set<DomainWarning> checkPatternsWarnings(
      Set<Class<? extends Annotation>> matchingAnnotations, Class<?> clazz) {
    return DDD_WARNING_PROCESSOR.keySet().stream()
        .filter(dddPattern -> matchingAnnotations.contains(dddPattern.relatedAnnotation()))
        .map(dddPattern -> checkPatternWarnings(dddPattern, clazz))
        .iterator()
        .next();
  }

  private static Set<DomainWarning> checkPatternWarnings(DDDPattern dddPattern, Class<?> clazz) {
    return DDD_WARNING_PROCESSOR.get(dddPattern).apply(clazz);
  }

  private static Set<DomainWarning> checkBoundedContextWarnings(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkSharedKernelWarnings(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkAggregateWarnings(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkDomainCommandWarnings(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkDomainEventWarnings(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkValueObjectWarnings(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkFactoryWarnings(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkRepositoryWarnings(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkDomainServiceWarnings(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkEntityWarnings(Class<?> clazz) {
    Set<DomainWarning> warnings = new HashSet<>();
    Set<Field> domainEntityIds =
        Arrays.stream(clazz.getDeclaredFields())
            .filter(
                field ->
                    field.isAnnotationPresent(io.candydoc.ddd.annotations.DomainEntityId.class))
            .collect(Collectors.toUnmodifiableSet());

    if (domainEntityIds.isEmpty()) {
      warnings.add(DomainWarning.NO_ID);
    }
    return warnings;
  }
}
