package io.candydoc.ddd.extract_domain_vocabulary;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class AnInvalidSharedKernelTest {

    DomainVocabularyFinder domainVocabularyFinder;
    UbiquitousLanguageRepository ubiquitousLanguageRepository;
    ExtractDomainModelsUseCase useCase;

    @BeforeEach
    public void setUp() {
        this.domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
        this.ubiquitousLanguageRepository = mock(UbiquitousLanguageRepository.class);
        this.useCase = new ExtractDomainModelsUseCase(domainVocabularyFinder, ubiquitousLanguageRepository);
    }

    @Test
    public void nested_strategic_patterns_are_forbidden() {
        // when
        ThrowableAssert.ThrowingCallable callable = () -> useCase.execute(ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.invalid_shared_kernel")
            .build());

        // then
        verify(ubiquitousLanguageRepository, never()).saveAll(any());

        assertThatThrownBy(callable)
            .isInstanceOf(ExtractionException.class)
            .isInstanceOf(NestedStrategicPatternsForbidden.class)
            .hasMessageStartingWith("'InvalidSharedKernel' contains nested strategic patterns:")
            .hasMessageContaining("- io.candydoc.ddd.sample.invalid_shared_kernel.nested_bounded_context.package-info")
            .hasMessageContaining("- io.candydoc.ddd.sample.invalid_shared_kernel.nested_shared_kernel.package-info");
    }

}
