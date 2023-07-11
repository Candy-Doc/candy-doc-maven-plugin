package io.candydoc.ddd.tactical_patterns.domain_service;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.FoundDomainObjects;
import io.candydoc.ddd.model.FullName;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.DomainService;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindDomainServiceTest {
  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.tactical_patterns.domain_service"));
  }

  @Test
  public void find_domain_service_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            DomainService.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_service"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.domain_service.FindDomainServiceTest$DomainServiceOne"))
                .simpleName(SimpleName.of("DomainServiceOne"))
                .description(Description.of("Description of the domain service"))
                .build());
  }

  @Test
  public void attributes_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            DomainService.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_service"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.domain_service.FindDomainServiceTest$DomainServiceWithoutAttributes"))
                .simpleName(SimpleName.of("DomainServiceWithoutAttributes"))
                .description(Description.of(""))
                .build());
  }

  @io.candydoc.ddd.annotations.DomainService(description = "Description of the domain service")
  public static class DomainServiceOne {}

  @io.candydoc.ddd.annotations.DomainService
  public static class DomainServiceWithoutAttributes {}
}
