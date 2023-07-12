package io.candydoc.ddd.tactical_patterns.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.FoundDomainObjects;
import io.candydoc.ddd.model.FullName;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.DomainRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindDomainRepositoryTest {
  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.tactical_patterns.repository"));
  }

  @Test
  public void find_repository_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            DomainRepository.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.repository"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.repository.FindDomainRepositoryTest$RepositoryOne"))
                .simpleName(SimpleName.of("RepositoryOne"))
                .description(Description.of("Description of the repository"))
                .build());
  }

  @Test
  public void description_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            DomainRepository.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.repository"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.repository.FindDomainRepositoryTest$RepositoryWithoutAttributes"))
                .simpleName(SimpleName.of("RepositoryWithoutAttributes"))
                .description(Description.of(""))
                .build());
  }

  @io.candydoc.ddd.annotations.DomainRepository(description = "Description of the repository")
  public static class RepositoryOne {}

  @io.candydoc.ddd.annotations.DomainRepository
  public static class RepositoryWithoutAttributes {}
}
