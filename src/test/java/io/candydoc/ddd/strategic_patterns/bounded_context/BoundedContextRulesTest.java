package io.candydoc.ddd.strategic_patterns.bounded_context;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.StrategicPattern;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoundedContextRulesTest {

    static DomainModel boundedContext;

    @BeforeAll
    static void setUp() {
        boundedContext = BoundedContext.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.BoundedContextOne"))
            .simpleName(SimpleName.of("BoundedContextOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
                .pattern(TacticalPattern.VALUE_OBJECT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
                .pattern(TacticalPattern.DOMAIN_COMMAND)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
                .pattern(TacticalPattern.DOMAIN_EVENT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
                .pattern(TacticalPattern.AGGREGATE)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.BoundedContextTwo"))
                .pattern(StrategicPattern.BOUNDED_CONTEXT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.SharedKernelOne"))
                .pattern(StrategicPattern.SHARED_KERNEL)
                .build())
            .build();
    }

    @Test
    void is_a_bounded_context() {
        assertThat(boundedContext.dddPattern())
            .isEqualTo(StrategicPattern.BOUNDED_CONTEXT);
    }

    @Test
    void it_depends_on_tactical_patterns_inside_the_same_package() {
        assertThat(boundedContext.allowedDependencies())
            .containsExactlyInAnyOrder(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
                    .pattern(TacticalPattern.VALUE_OBJECT)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
                    .pattern(TacticalPattern.DOMAIN_COMMAND)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
                    .pattern(TacticalPattern.DOMAIN_EVENT)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
                    .pattern(TacticalPattern.AGGREGATE)
                    .build());
    }

    @Test
    void it_can_not_have_nested_bounded_contexts_nor_shared_kernels() {
        assertThat(boundedContext.forbiddenDependencies())
            .containsExactlyInAnyOrder(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.BoundedContextTwo"))
                    .pattern(StrategicPattern.BOUNDED_CONTEXT)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.SharedKernelOne"))
                    .pattern(StrategicPattern.SHARED_KERNEL)
                    .build());
    }
}
