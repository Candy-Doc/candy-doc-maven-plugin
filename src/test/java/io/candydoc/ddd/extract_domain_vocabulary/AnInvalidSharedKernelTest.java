package io.candydoc.ddd.extract_domain_vocabulary;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AnInvalidSharedKernelTest {

  @Spy DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  @Mock UbiquitousLanguageRepository ubiquitousLanguageRepository;
  @InjectMocks ExtractDomainModelsUseCase useCase;

  @Test
  public void nested_strategic_patterns_are_forbidden() {
    // when
    ThrowableAssert.ThrowingCallable callable =
        () ->
            useCase.execute(
                ExtractDomainModels.builder()
                    .packageToScan("io.candydoc.ddd.sample.invalid_shared_kernel")
                    .build());

    // then
    verify(ubiquitousLanguageRepository, never()).saveAll(any());

    assertThatThrownBy(callable)
        .isInstanceOf(ExtractionException.class)
        .isInstanceOf(NestedStrategicPatternsForbidden.class)
        .hasMessageStartingWith("'InvalidSharedKernel' contains nested strategic patterns:")
        .hasMessageContaining(
            "- io.candydoc.ddd.sample.invalid_shared_kernel.nested_bounded_context.package-info")
        .hasMessageContaining(
            "- io.candydoc.ddd.sample.invalid_shared_kernel.nested_shared_kernel.package-info");
  }
}
