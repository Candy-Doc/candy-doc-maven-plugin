package io.candydoc.ddd.extract_domain_models;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import org.assertj.core.api.Assertions;
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
public class OneValidBoundedContextTest {

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
    public void extract_ubiquitous_language_from_bounded_context() {
        // when
        useCase.execute(ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.one_valid_bounded_context")
            .build());

        // then
        verify(ubiquitousLanguageRepository).saveAll(ubiquitousLanguagesCaptor.capture());

        assertThat(ubiquitousLanguagesCaptor.getValue())
            .hasSize(1);

        assertThat(ubiquitousLanguagesCaptor.getValue()).first()
            .isInstanceOf(UbiquitousLanguage.BoundedVocabulary.class)
            .satisfies(ubiquitousLanguage -> {
                UbiquitousLanguage.BoundedVocabulary vocabulary = (UbiquitousLanguage.BoundedVocabulary) ubiquitousLanguage;

                assertThat(vocabulary.boundedContext())
                    .isEqualTo(BoundedContext.builder()
                        .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.package-info"))
                        .simpleName(SimpleName.of("Bounded context one"))
                        .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context"))
                        .description(Description.of("One valid bounded context"))
                        .build());

                assertThat(vocabulary.domainModels())
                    .containsExactlyInAnyOrder(
                        Aggregate.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.AggregateOne"))
                            .simpleName(SimpleName.of("AggregateOne"))
                            .description(Description.of(""))
                            .build(),
                        DomainCommand.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainCommandOne"))
                            .simpleName(SimpleName.of("DomainCommandOne"))
                            .description(Description.of(""))
                            .build(),
                        DomainEvent.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainEventOne"))
                            .simpleName(SimpleName.of("DomainEventOne"))
                            .description(Description.of(""))
                            .build(),
                        ValueObject.builder()
                            .packageName(PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                            .canonicalName(CanonicalName.of("io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.ValueObjectOne"))
                            .simpleName(SimpleName.of("ValueObjectOne"))
                            .description(Description.of(""))
                            .build()
                    );
            });
    }

}
