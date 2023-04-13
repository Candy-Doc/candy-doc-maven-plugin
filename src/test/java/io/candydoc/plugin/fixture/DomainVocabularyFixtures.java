package io.candydoc.plugin.fixture;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.strategic_pattern.StrategicPattern;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.util.Set;

public class DomainVocabularyFixtures {

  public static Description EMPTY_DESCRIPTION = Description.of("");

  public static final Set<FoundDomainObject> DOMAIN_OBJECTS_IN_BOUNDED_CONTEXT =
      Set.of(
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(StrategicPattern.BOUNDED_CONTEXT)
                      .name(SimpleName.of("Bounded context one"))
                      .description(Description.of("Describe the bounded context here"))
                      .build())
              .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context"))
              .canonicalName(
                  CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.package-info"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.AggregateOne"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainCommandOne"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainEventOne"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.ValueObjectOne"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.EntityOne"))
              .build(),
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.AGGREGATE)
                      .name(SimpleName.of("AggregateOne"))
                      .description(EMPTY_DESCRIPTION)
                      .build())
              .packageName(
                  PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
              .canonicalName(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.AggregateOne"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainCommandOne"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainEventOne"))
              .build(),
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.DOMAIN_COMMAND)
                      .name(SimpleName.of("DomainCommandOne"))
                      .description(EMPTY_DESCRIPTION)
                      .build())
              .packageName(
                  PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
              .canonicalName(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainCommandOne"))
              .build(),
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.DOMAIN_EVENT)
                      .name(SimpleName.of("DomainEventOne"))
                      .description(EMPTY_DESCRIPTION)
                      .build())
              .packageName(
                  PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
              .canonicalName(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainEventOne"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.EntityOne"))
              .build(),
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.ENTITY)
                      .name(SimpleName.of("EntityOne"))
                      .description(EMPTY_DESCRIPTION)
                      .build())
              .packageName(
                  PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
              .canonicalName(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.EntityOne"))
              .build(),
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.VALUE_OBJECT)
                      .name(SimpleName.of("ValueObjectOne"))
                      .description(EMPTY_DESCRIPTION)
                      .build())
              .packageName(
                  PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
              .canonicalName(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.ValueObjectOne"))
              .build());

  public static final Set<FoundDomainObject> DOMAIN_OBJECTS_IN_SHARED_KERNEL =
      Set.of(
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(StrategicPattern.SHARED_KERNEL)
                      .name(SimpleName.of("Shared kernel one"))
                      .description(Description.of("One valid shared kernel"))
                      .build())
              .canonicalName(
                  CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.package-info"))
              .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A.SharedValueObjectOne"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B.SharedValueObjectTwo"))
              .build(),
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.VALUE_OBJECT)
                      .name(SimpleName.of("SharedValueObjectOne"))
                      .description(Description.of("Some description"))
                      .build())
              .packageName(
                  PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A"))
              .canonicalName(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A.SharedValueObjectOne"))
              .dependency(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B.SharedValueObjectTwo"))
              .build(),
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.VALUE_OBJECT)
                      .name(SimpleName.of("SharedValueObjectTwo"))
                      .description(EMPTY_DESCRIPTION)
                      .build())
              .packageName(
                  PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B"))
              .canonicalName(
                  CanonicalName.of(
                      "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B.SharedValueObjectTwo"))
              .build());

  public static final Set<FoundDomainObject> ORPHAN_DOMAIN_OBJECTS =
      Set.of(
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.VALUE_OBJECT)
                      .name(SimpleName.of("OrphanValueObjectOne"))
                      .description(Description.of("An orphan value object"))
                      .build())
              .packageName(PackageName.of("io.candydoc.ddd.sample.orphan"))
              .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.orphan.OrphanValueObjectOne"))
              .dependency(CanonicalName.of("io.candydoc.ddd.sample.orphan.OrphanDomainEventOne"))
              .build(),
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.DOMAIN_EVENT)
                      .name(SimpleName.of("OrphanDomainEventOne"))
                      .description(Description.of("An orphan domain event"))
                      .build())
              .packageName(PackageName.of("io.candydoc.ddd.sample.orphan"))
              .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.orphan.OrphanDomainEventOne"))
              .dependency(CanonicalName.of("io.candydoc.ddd.sample.orphan.OrphanDomainCommandOne"))
              .build(),
          FoundDomainObject.builder()
              .matchingPattern(
                  FoundDDDPattern.builder()
                      .dddPattern(TacticalPattern.DOMAIN_COMMAND)
                      .name(SimpleName.of("OrphanDomainCommandOne"))
                      .description(Description.of("An orphan domain command"))
                      .build())
              .packageName(PackageName.of("io.candydoc.ddd.sample.orphan"))
              .canonicalName(
                  CanonicalName.of("io.candydoc.ddd.sample.orphan.OrphanDomainCommandOne"))
              .build());
}
