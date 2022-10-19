package io.candydoc.ddd.strategic_patterns.bounded_context;

import io.candydoc.ddd.extract_domain_models.DomainModelFinder;
import io.candydoc.ddd.extract_domain_models.ReflectionsDomainModelFinder;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FindBoundedContextTest {

    static DomainModelFinder domainModelFinder = new ReflectionsDomainModelFinder(List.of("io.candydoc.ddd.strategic_patterns.bounded_context"));

    @Test
    public void find_bounded_context_from_classes() {
        // when
        Set<DomainModel> actualDomainModels = domainModelFinder.findDomainModels(PackageName.of("io.candydoc.ddd.strategic_patterns.bounded_context"));

        // then
        assertThat(actualDomainModels)
            .contains(BoundedContext.builder()
                .packageName(PackageName.of("io.candydoc.ddd.strategic_patterns.bounded_context.sample"))
                .canonicalName(CanonicalName.of("io.candydoc.ddd.strategic_patterns.bounded_context.sample.package-info"))
                .simpleName(SimpleName.of("Bounded context one"))
                .description(Description.of("Describe the bounded context here"))
                .build());
    }
}
