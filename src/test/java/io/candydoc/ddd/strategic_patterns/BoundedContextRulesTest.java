package io.candydoc.ddd.strategic_patterns;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
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
                .pattern(DDDPattern.Tactical.VALUE_OBJECT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
                .pattern(DDDPattern.Tactical.DOMAIN_COMMAND)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
                .pattern(DDDPattern.Tactical.DOMAIN_EVENT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
                .pattern(DDDPattern.Tactical.AGGREGATE)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.BoundedContextTwo"))
                .pattern(DDDPattern.Strategic.BOUNDED_CONTEXT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.SharedKernelOne"))
                .pattern(DDDPattern.Strategic.SHARED_KERNEL)
                .build())
            .build();
    }

    @Test
    void is_a_bounded_context() {
        assertThat(boundedContext.dddPattern())
            .isEqualTo(DDDPattern.Strategic.BOUNDED_CONTEXT);
    }

    @Test
    void it_depends_on_itsef_only() {
        assertThat(boundedContext.allowedDependencies())
            .isEmpty();

        assertThat(boundedContext.forbiddenDependencies())
            .containsExactlyInAnyOrder(Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
                    .pattern(DDDPattern.Tactical.VALUE_OBJECT)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
                    .pattern(DDDPattern.Tactical.DOMAIN_COMMAND)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
                    .pattern(DDDPattern.Tactical.DOMAIN_EVENT)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
                    .pattern(DDDPattern.Tactical.AGGREGATE)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.BoundedContextTwo"))
                    .pattern(DDDPattern.Strategic.BOUNDED_CONTEXT)
                    .build(),
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.SharedKernelOne"))
                    .pattern(DDDPattern.Strategic.SHARED_KERNEL)
                    .build());
    }

}
