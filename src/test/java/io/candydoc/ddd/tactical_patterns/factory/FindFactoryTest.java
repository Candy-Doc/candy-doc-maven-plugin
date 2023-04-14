package io.candydoc.ddd.tactical_patterns.factory;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.FoundDomainObjects;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.Factory;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindFactoryTest {
  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.tactical_patterns.factory"));
  }

  @Test
  public void find_entity_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            Factory.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.factory"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.tactical_patterns.factory.FindFactoryTest.FactoryOne"))
                .simpleName(SimpleName.of("FactoryOne"))
                .description(Description.of("Description of the factory"))
                .build());
  }

  @Test
  public void attributes_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            Factory.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.factory"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.tactical_patterns.factory.FindFactoryTest.FactoryWithoutAttributes"))
                .simpleName(SimpleName.of("FactoryWithoutAttributes"))
                .description(Description.of(""))
                .build());
  }

  @io.candydoc.ddd.annotations.Factory(description = "Description of the factory")
  public static class FactoryOne {}

  @io.candydoc.ddd.annotations.Factory
  public static class FactoryWithoutAttributes {}
}
