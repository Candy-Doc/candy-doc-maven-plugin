package io.candydoc.ddd.strategic_patterns.shared_kernel;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.strategic_pattern.StrategicPattern;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SharedKernelRulesTest {

    static DomainModel sharedKernel;

    @BeforeAll
    static void setUp() {
        sharedKernel = SharedKernel.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.SharedKernelOne"))
            .simpleName(SimpleName.of("SharedKernelOne"))
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
                .name(CanonicalName.of("io.candydoc.sample.BoundedContextOne"))
                .pattern(StrategicPattern.BOUNDED_CONTEXT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.SharedKernelTwo"))
                .pattern(StrategicPattern.SHARED_KERNEL)
                .build())
            .build();
    }

    @Test
    void is_a_shared_kernel() {
        assertThat(sharedKernel.dddPattern())
            .isEqualTo(StrategicPattern.SHARED_KERNEL);
    }

    @Test
    void it_can_contain_value_objects() {
        assertThat(sharedKernel.allowedDependencies())
            .containsExactlyInAnyOrder(
                Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
                .pattern(TacticalPattern.VALUE_OBJECT)
                .build());
    }

    @Test
    void it_can_not_contains_other_tactical_patterns_than_value_object() {
        assertThat(sharedKernel.forbiddenDependencies())
            .contains(
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
        assertThat(sharedKernel.forbiddenDependencies())
            .contains(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.BoundedContextOne"))
                    .pattern(StrategicPattern.BOUNDED_CONTEXT)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.SharedKernelTwo"))
                    .pattern(StrategicPattern.SHARED_KERNEL)
                    .build());
    }
}
