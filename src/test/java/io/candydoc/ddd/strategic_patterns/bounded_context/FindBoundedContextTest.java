package io.candydoc.ddd.strategic_patterns.bounded_context;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindBoundedContextTest {

  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.strategic_patterns.bounded_context"));
  }

  @Test
  public void find_bounded_context_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            BoundedContext.builder()
                .packageName(
                    PackageName.of("io.candydoc.ddd.strategic_patterns.bounded_context.sampleA"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.strategic_patterns.bounded_context.sampleA.package-info"))
                .simpleName(SimpleName.of("Bounded context one"))
                .description(Description.of("Describe the bounded context here"))
                .build());
  }

  @Test
  public void attributes_are_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            BoundedContext.builder()
                .packageName(
                    PackageName.of("io.candydoc.ddd.strategic_patterns.bounded_context.sampleB"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.strategic_patterns.bounded_context.sampleB.package-info"))
                .simpleName(SimpleName.of("sampleB"))
                .description(Description.of(""))
                .build());
  }
}
