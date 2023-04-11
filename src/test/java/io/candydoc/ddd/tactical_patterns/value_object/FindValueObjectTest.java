package io.candydoc.ddd.tactical_patterns.value_object;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindValueObjectTest {

  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.tactical_patterns.value_object"));
  }

  @Test
  public void find_value_object_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            ValueObject.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.value_object"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.value_object.FindValueObjectTest$ValueObjectOne"))
                .simpleName(SimpleName.of("ValueObjectOne"))
                .description(Description.of("Describe the value object here"))
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.VALUE_OBJECT)
                        .name(
                            FullName.of(
                                "io.candydoc.ddd.tactical_patterns.value_object.FindValueObjectTest$ValueObjectTwo"))
                        .build())
                .build());
  }

  @Test
  public void attributes_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            ValueObject.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.value_object"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.value_object.FindValueObjectTest$ValueObjectTwo"))
                .simpleName(SimpleName.of("ValueObjectTwo"))
                .description(Description.of(""))
                .build());
  }

  @io.candydoc.ddd.annotations.ValueObject(description = "Describe the value object here")
  public static class ValueObjectOne {
    private ValueObjectTwo valueObjectTwo;
  }

  @io.candydoc.ddd.annotations.ValueObject
  public static class ValueObjectTwo {}

  @io.candydoc.ddd.annotations.ValueObject
  public enum EnumValueObject {
    A {
      @Override
      void someMethod() {}
    },
    B {
      @Override
      void someMethod() {}
    };

    abstract void someMethod();
  }
}
