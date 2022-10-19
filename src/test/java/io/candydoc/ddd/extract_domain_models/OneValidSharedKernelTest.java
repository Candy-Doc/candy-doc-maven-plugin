package io.candydoc.ddd.extract_domain_models;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OneValidSharedKernelTest {

    DomainModelFinder domainModelFinder;
    UbiquitousLanguageRepository ubiquitousLanguageRepository;
    ExtractDomainModelsUseCase useCase;

    @Captor
    private ArgumentCaptor<Set<UbiquitousLanguage>> ubiquitousLanguagesCaptor;

    @BeforeEach
    public void setUp() {
        this.domainModelFinder = new ReflectionsDomainModelFinder(List.of("io.candydoc.ddd.sample"));
        this.ubiquitousLanguageRepository = mock(UbiquitousLanguageRepository.class);
        this.useCase = new ExtractDomainModelsUseCase(domainModelFinder, ubiquitousLanguageRepository);
    }

    @Test
    public void extract_ubiquitous_language_from_shared_kernel() {
        // when
        useCase.execute(ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.one_valid_shared_kernel")
            .build());

        // then
        verify(ubiquitousLanguageRepository).saveAll(ubiquitousLanguagesCaptor.capture());

        assertThat(ubiquitousLanguagesCaptor.getValue())
            .hasSize(1);

        assertThat(ubiquitousLanguagesCaptor.getValue()).first()
            .isInstanceOf(UbiquitousLanguage.SharedVocabulary.class)
            .satisfies(ubiquitousLanguage -> {
                UbiquitousLanguage.SharedVocabulary vocabulary = (UbiquitousLanguage.SharedVocabulary) ubiquitousLanguage;

                assertThat(vocabulary.sharedKernel())
                    .isEqualTo(SharedKernel.builder()
                        .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.package-info"))
                        .simpleName(SimpleName.of("Shared kernel one"))
                        .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel"))
                        .description(Description.of("One valid shared kernel"))
                        .build());

                assertThat(vocabulary.domainModels())
                    .containsExactlyInAnyOrder(
                        ValueObject.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A.SharedValueObjectOne"))
                            .simpleName(SimpleName.of("SharedValueObjectOne"))
                            .description(Description.of(""))
                            .build(),
                        ValueObject.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B.SharedValueObjectTwo"))
                            .simpleName(SimpleName.of("SharedValueObjectTwo"))
                            .description(Description.of(""))
                            .build()
                    );
            });
    }

}
