package io.candydoc.ddd.extract_domain_models;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.model.UbiquitousLanguage;
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
public class OrphanVocabularyTest {

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
    public void extract_ubiquitous_language_that_is_not_in_a_bounded_context_nor_a_shared_kernel() {
        // when
        useCase.execute(ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.just_a_package")
            .build());

        // then
        verify(ubiquitousLanguageRepository).saveAll(ubiquitousLanguagesCaptor.capture());

        assertThat(ubiquitousLanguagesCaptor.getValue())
            .hasSize(1);

        assertThat(ubiquitousLanguagesCaptor.getValue()).first()
            .isInstanceOf(UbiquitousLanguage.OrphanBoundedVocabulary.class)
            .satisfies(ubiquitousLanguage -> {
                UbiquitousLanguage.OrphanBoundedVocabulary vocabulary = (UbiquitousLanguage.OrphanBoundedVocabulary) ubiquitousLanguage;

                assertThat(vocabulary.domainModels())
                    .containsExactlyInAnyOrder(
                        Aggregate.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.just_a_package.feature_A"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.just_a_package.feature_A.AggregateOne"))
                            .simpleName(SimpleName.of("AggregateOne"))
                            .description(Description.of(""))
                            .build(),
                        DomainCommand.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.just_a_package.feature_A"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.just_a_package.feature_A.DomainCommandOne"))
                            .simpleName(SimpleName.of("DomainCommandOne"))
                            .description(Description.of(""))
                            .build(),
                        DomainEvent.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.just_a_package.feature_A"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.just_a_package.feature_A.DomainEventOne"))
                            .simpleName(SimpleName.of("DomainEventOne"))
                            .description(Description.of(""))
                            .build(),
                        ValueObject.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.just_a_package.feature_A"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.just_a_package.feature_A.ValueObjectOne"))
                            .simpleName(SimpleName.of("ValueObjectOne"))
                            .description(Description.of(""))
                            .build()
                    );
            });
    }

}
