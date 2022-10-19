package io.candydoc.ddd.tactical_patterns.domain_command;

import io.candydoc.ddd.extract_domain_models.DomainModelFinder;
import io.candydoc.ddd.extract_domain_models.ReflectionsDomainModelFinder;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FindDomainCommandTest {

    static DomainModelFinder domainModelFinder = new ReflectionsDomainModelFinder(List.of("io.candydoc.ddd.tactical_patterns.domain_command"));

    @Test
    public void find_domain_command_from_classes() {
        // when
        Set<DomainModel> actualDomainModels = domainModelFinder.findDomainModels(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_command"));

        // then
        assertThat(actualDomainModels)
            .contains(Aggregate.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_command"))
                .canonicalName(CanonicalName.of("io.candydoc.ddd.tactical_patterns.domain_command.FindDomainCommandTest.DomainCommandOne"))
                .simpleName(SimpleName.of("DomainCommandOne"))
                .description(Description.of("Describe the domain command here"))
                .dependency(Dependency.builder()
                    .pattern(DDDPattern.Tactical.VALUE_OBJECT)
                    .name(CanonicalName.of("io.candydoc.ddd.tactical_patterns.domain_command.FindDomainCommandTest.ValueObjectOne"))
                    .build())
                .build());
    }

    @io.candydoc.ddd.annotations.DomainCommand(description = "Describe the domain command here")
    public static class DomainCommandOne {
        private ValueObjectOne valueObjectOne;
    }

    @io.candydoc.ddd.annotations.ValueObject
    public static class ValueObjectOne {
    }

}
