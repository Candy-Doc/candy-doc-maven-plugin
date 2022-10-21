package io.candydoc.plugin.fixture;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.strategic_pattern.StrategicPattern;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;

import java.util.Set;

public class UbiquitousLanguageFixtures {

    public static final Set<FoundDomainObject> DOMAIN_OBJECTS_IN_BOUNDED_CONTEXT = Set.of(
            FoundDomainObject.builder()
                    .matchingPattern(FoundDDDPattern.builder()
                            .dddPattern(StrategicPattern.BOUNDED_CONTEXT)
                            .name(SimpleName.of("Bounded context one"))
                            .description(Description.of("Describe the bounded context here"))
                            .build())
                    .packageName(PackageName.of("io.candydoc.ddd.strategic_patterns.bounded_context.sample"))
                    .canonicalName(CanonicalName.of("io.candydoc.ddd.strategic_patterns.bounded_context.sample.package-info"))
                    .build(),
            FoundDomainObject.builder()
                    .matchingPattern(FoundDDDPattern.builder()
                            .dddPattern(TacticalPattern.AGGREGATE)
                            .name(SimpleName.of("AggregateOne"))
                            .build())
                    .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                    .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.AggregateOne"))
                    .build(),
            FoundDomainObject.builder()
                    .matchingPattern(FoundDDDPattern.builder()
                            .dddPattern(TacticalPattern.DOMAIN_COMMAND)
                            .name(SimpleName.of("DomainCommandOne"))
                            .build())
                    .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                    .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainCommandOne"))
                    .build(),
            FoundDomainObject.builder()
                    .matchingPattern(FoundDDDPattern.builder()
                            .dddPattern(TacticalPattern.DOMAIN_EVENT)
                            .name(SimpleName.of("DomainEventOne"))
                            .build())
                    .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                    .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainEventOne"))
                    .build(),
            FoundDomainObject.builder()
                    .matchingPattern(FoundDDDPattern.builder()
                            .dddPattern(TacticalPattern.VALUE_OBJECT)
                            .name(SimpleName.of("ValueObjectOne"))
                            .build())
                    .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                    .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.ValueObjectOne"))
                    .build());

    public static final Set<FoundDomainObject> DOMAIN_OBJECTS_IN_SHARED_KERNEL = Set.of(
            FoundDomainObject.builder()
                    .matchingPattern(FoundDDDPattern.builder()
                            .dddPattern(StrategicPattern.SHARED_KERNEL)
                            .name(SimpleName.of("Shared kernel one"))
                            .description(Description.of("One valid shared kernel"))
                            .build())
                    .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.package-info"))
                    .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel"))
                    .build(),
            FoundDomainObject.builder()
                    .matchingPattern(FoundDDDPattern.builder()
                            .dddPattern(TacticalPattern.VALUE_OBJECT)
                            .name(SimpleName.of("SharedValueObjectOne"))
                            .build())
                    .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A"))
                    .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A.SharedValueObjectOne"))
                    .build(),
            FoundDomainObject.builder()
                    .matchingPattern(FoundDDDPattern.builder()
                            .dddPattern(TacticalPattern.VALUE_OBJECT)
                            .name(SimpleName.of("SharedValueObjectTwo"))
                            .build())
                    .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B"))
                    .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B.SharedValueObjectTwo"))
                    .build()
    );

}
