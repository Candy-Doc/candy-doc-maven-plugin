package io.candydoc.ddd.tactical_patterns.value_object;

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

public class FindValueObjectTest {

    static DomainModelFinder domainModelFinder = new ReflectionsDomainModelFinder(List.of("io.candydoc.ddd.tactical_patterns.value_object"));

    @Test
    public void find_value_object_from_classes() {
        // when
        Set<DomainModel> actualDomainModels = domainModelFinder.findDomainModels(PackageName.of("io.candydoc.ddd.tactical_patterns.value_object"));

        // then
        assertThat(actualDomainModels)
            .contains(Aggregate.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.value_object"))
                .canonicalName(CanonicalName.of("io.candydoc.ddd.tactical_patterns.value_object.FindValueObjectTest.ValueObjectOne"))
                .simpleName(SimpleName.of("ValueObjectOne"))
                .description(Description.of("Describe the value object here"))
                .dependency(Dependency.builder()
                    .pattern(DDDPattern.Tactical.VALUE_OBJECT)
                    .name(CanonicalName.of("io.candydoc.ddd.tactical_patterns.value_object.FindValueObjectTest.ValueObjectTwo"))
                    .build())
                .build());
    }

    @io.candydoc.ddd.annotations.ValueObject(description = "Describe the value object here")
    public static class ValueObjectOne {
        private ValueObjectTwo valueObjectTwo;
    }

    @io.candydoc.ddd.annotations.ValueObject
    public static class ValueObjectTwo {

    }

}
