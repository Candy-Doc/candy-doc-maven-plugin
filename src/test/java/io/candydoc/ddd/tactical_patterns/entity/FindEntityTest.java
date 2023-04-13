package io.candydoc.ddd.tactical_patterns.entity;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.Entity;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FindEntityTest {

  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.tactical_patterns.entity"));
  }

  @Test
  public void find_entity_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            Entity.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.entity"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.tactical_patterns.entity.FindEntityTest.MyEntity"))
                .simpleName(SimpleName.of("My entity"))
                .description(Description.of("Description of the entity"))
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.VALUE_OBJECT)
                        .name(
                            CanonicalName.of(
                                "io.candydoc.ddd.tactical_patterns.entity.FindEntityTest.ValueObjectOne"))
                        .build())
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.VALUE_OBJECT)
                        .name(
                            CanonicalName.of(
                                "io.candydoc.ddd.tactical_patterns.entity.FindEntityTest.ValueObjectTwo"))
                        .build())
                .build());
  }

  @Test
  public void attributes_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            Entity.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.entity"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.tactical_patterns.entity.FindEntityTest.EntityWithoutAttributes"))
                .simpleName(SimpleName.of("EntityWithoutAttributes"))
                .description(Description.of(""))
                .build());
  }

  @io.candydoc.ddd.annotations.Entity(name = "My entity", description = "Description of the entity")
  public static class MyEntity {
    private ValueObjectOne valueObjectOne;
    private ValueObjectTwo valueObjectTwo;
  }

  @io.candydoc.ddd.annotations.ValueObject(description = "This is a value object")
  public static class ValueObjectOne {}

  @io.candydoc.ddd.annotations.ValueObject
  public static class ValueObjectTwo {}

  @io.candydoc.ddd.annotations.Entity
  public static class EntityWithoutAttributes {}
}
