package io.candydoc.ddd.tactical_patterns.entity;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.model.DomainWarning;
import io.candydoc.ddd.tactical_pattern.DomainEntity;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindDomainEntityTest {

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
            DomainEntity.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.entity"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.tactical_patterns.entity.FindDomainEntityTest.MyEntity"))
                .simpleName(SimpleName.of("MyEntity"))
                .description(Description.of("Description of the entity"))
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.VALUE_OBJECT)
                        .name(
                            CanonicalName.of(
                                "io.candydoc.ddd.tactical_patterns.entity.FindDomainEntityTest.ValueObjectOne"))
                        .build())
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.VALUE_OBJECT)
                        .name(
                            CanonicalName.of(
                                "io.candydoc.ddd.tactical_patterns.entity.FindDomainEntityTest.ValueObjectTwo"))
                        .build())
                .build());
  }

  @Test
  public void id_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            io.candydoc.ddd.tactical_pattern.DomainEntity.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.entity"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.tactical_patterns.entity.FindDomainEntityTest.EntityWithoutId"))
                .simpleName(SimpleName.of("EntityWithoutId"))
                .description(Description.of(""))
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.VALUE_OBJECT)
                        .name(
                            CanonicalName.of(
                                "io.candydoc.ddd.tactical_patterns.entity.FindDomainEntityTest.ValueObjectOne"))
                        .build())
                .warning(DomainWarning.NO_ID)
                .build());
  }

  @Test
  public void attributes_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            io.candydoc.ddd.tactical_pattern.DomainEntity.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.entity"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.tactical_patterns.entity.FindDomainEntityTest.EntityWithoutAttributes"))
                .simpleName(SimpleName.of("EntityWithoutAttributes"))
                .description(Description.of(""))
                .warning(DomainWarning.NO_ID)
                .build());
  }

  @io.candydoc.ddd.annotations.DomainEntity(description = "Description of the entity")
  public static class MyEntity {
    @io.candydoc.ddd.annotations.DomainEntityId private ValueObjectOne valueObjectOne;
    @io.candydoc.ddd.annotations.DomainEntityId private ValueObjectTwo valueObjectTwo;
  }

  @io.candydoc.ddd.annotations.ValueObject(description = "This is a value object")
  public static class ValueObjectOne {}

  @io.candydoc.ddd.annotations.ValueObject
  public static class ValueObjectTwo {}

  @io.candydoc.ddd.annotations.DomainEntity
  public static class EntityWithoutId {
    private ValueObjectOne id;
  }

  @io.candydoc.ddd.annotations.DomainEntity
  public static class EntityWithoutAttributes {}
}
