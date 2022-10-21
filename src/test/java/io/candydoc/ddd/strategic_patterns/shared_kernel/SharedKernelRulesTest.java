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
    void it_depends_on_itsef_only() {
        assertThat(sharedKernel.allowedDependencies())
            .isEmpty();

        assertThat(sharedKernel.forbiddenDependencies())
            .containsExactlyInAnyOrder(Dependency.builder()
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
                    .build(),
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
