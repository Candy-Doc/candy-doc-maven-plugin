package io.candydoc.ddd.strategic_patterns.shared_kernel;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindSharedKernelTest {

  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.strategic_patterns.shared_kernel"));
  }

  @Test
  public void find_shared_kernel_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            SharedKernel.builder()
                .packageName(
                    PackageName.of("io.candydoc.ddd.strategic_patterns.shared_kernel.sampleA"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.strategic_patterns.shared_kernel.sampleA.package-info"))
                .simpleName(SimpleName.of("Shared kernel one"))
                .description(Description.of("Describe the shared kernel here"))
                .build());
  }

  @Test
  public void attributes_are_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            SharedKernel.builder()
                .packageName(
                    PackageName.of("io.candydoc.ddd.strategic_patterns.shared_kernel.sampleB"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.strategic_patterns.shared_kernel.sampleB.package-info"))
                .simpleName(SimpleName.of("sampleB"))
                .description(Description.of(""))
                .build());
  }
}
