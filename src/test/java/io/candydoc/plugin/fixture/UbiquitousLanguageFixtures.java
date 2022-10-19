package io.candydoc.plugin.fixture;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.ValueObject;

import java.util.Set;

public class UbiquitousLanguageFixtures {

    public static final Set<DomainModel> BOUNDED_CONTEXT = Set.of(BoundedContext.builder()
            .packageName(PackageName.of("io.candydoc.ddd.strategic_patterns.bounded_context.sample"))
            .canonicalName(CanonicalName.of("io.candydoc.ddd.strategic_patterns.bounded_context.sample.package-info"))
            .simpleName(SimpleName.of("Bounded context one"))
            .description(Description.of("Describe the bounded context here"))
            .build(),
        Aggregate.builder()
            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.AggregateOne"))
            .simpleName(SimpleName.of("AggregateOne"))
            .description(Description.of(""))
            .build(),
        DomainCommand.builder()
            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainCommandOne"))
            .simpleName(SimpleName.of("DomainCommandOne"))
            .description(Description.of(""))
            .build(),
        DomainEvent.builder()
            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainEventOne"))
            .simpleName(SimpleName.of("DomainEventOne"))
            .description(Description.of(""))
            .build(),
        ValueObject.builder()
            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.ValueObjectOne"))
            .simpleName(SimpleName.of("ValueObjectOne"))
            .description(Description.of(""))
            .build());

    public static final Set<DomainModel> SHARED_KERNEL = Set.of(
        SharedKernel.builder()
            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.package-info"))
            .simpleName(SimpleName.of("Shared kernel one"))
            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel"))
            .description(Description.of("One valid shared kernel"))
            .build(),
        ValueObject.builder()
            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A"))
            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A.SharedValueObjectOne"))
            .simpleName(SimpleName.of("SharedValueObjectOne"))
            .description(Description.of(""))
            .build(),
        ValueObject.builder()
            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B"))
            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B.SharedValueObjectTwo"))
            .simpleName(SimpleName.of("SharedValueObjectTwo"))
            .description(Description.of(""))
            .build()
    );

}
