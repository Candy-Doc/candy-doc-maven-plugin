package io.candydoc.ddd.strategic_patterns.shared_kernel;

import io.candydoc.ddd.extract_domain_models.DomainModelFinder;
import io.candydoc.ddd.extract_domain_models.ReflectionsDomainModelFinder;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FindSharedKernelTest {

    static DomainModelFinder domainModelFinder = new ReflectionsDomainModelFinder(List.of("io.candydoc.ddd.strategic_patterns.shared_kernel"));

    @Test
    public void find_shared_kernel_from_classes() {
        // when
        Set<DomainModel> actualDomainModels = domainModelFinder.findDomainModels(PackageName.of("io.candydoc.ddd.strategic_patterns.shared_kernel"));

        // then
        assertThat(actualDomainModels)
            .contains(SharedKernel.builder()
                .packageName(PackageName.of("io.candydoc.ddd.strategic_patterns.shared_kernel.sample"))
                .canonicalName(CanonicalName.of("io.candydoc.ddd.strategic_patterns.shared_kernel.sample.package-info"))
                .simpleName(SimpleName.of("Shared kernel one"))
                .description(Description.of("Describe the shared kernel here"))
                .build());
    }
}
