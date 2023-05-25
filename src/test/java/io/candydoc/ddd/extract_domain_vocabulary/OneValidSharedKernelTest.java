package io.candydoc.ddd.extract_domain_vocabulary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OneValidSharedKernelTest {

  @Spy DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  @Mock UbiquitousLanguageRepository ubiquitousLanguageRepository;
  @InjectMocks ExtractDomainModelsUseCase useCase;
  @Captor private ArgumentCaptor<Set<UbiquitousLanguage>> ubiquitousLanguagesCaptor;

  @Test
  public void extract_ubiquitous_language_from_shared_kernel() {
    // when
    useCase.execute(
        ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.one_valid_shared_kernel")
            .build());

    // then
    verify(ubiquitousLanguageRepository).saveAll(ubiquitousLanguagesCaptor.capture());

    assertThat(ubiquitousLanguagesCaptor.getValue()).hasSize(1);

    assertThat(ubiquitousLanguagesCaptor.getValue())
        .first()
        .isInstanceOf(UbiquitousLanguage.SharedVocabulary.class)
        .satisfies(
            ubiquitousLanguage -> {
              UbiquitousLanguage.SharedVocabulary vocabulary =
                  (UbiquitousLanguage.SharedVocabulary) ubiquitousLanguage;

              assertThat(vocabulary.sharedKernel())
                  .isEqualTo(
                      SharedKernel.builder()
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.one_valid_shared_kernel.package-info"))
                          .simpleName(SimpleName.of("Shared kernel one"))
                          .packageName(
                              PackageName.of("io.candydoc.ddd.sample.one_valid_shared_kernel"))
                          .description(Description.of("One valid shared kernel"))
                          .relation(
                              Relations.of("io.candydoc.ddd.sample.one_valid_bounded_context"))
                          .relation(
                              Relations.of("io.candydoc.ddd.sample.second_valid_bounded_context"))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      FullName.of(
                                          "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A.SharedValueObjectOne"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      FullName.of(
                                          "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B.SharedValueObjectTwo"))
                                  .build())
                          .build());

              assertThat(vocabulary.domainModels())
                  .containsExactlyInAnyOrder(
                      ValueObject.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A"))
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_A.SharedValueObjectOne"))
                          .simpleName(SimpleName.of("SharedValueObjectOne"))
                          .description(Description.of(""))
                          .build(),
                      ValueObject.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B"))
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.one_valid_shared_kernel.shared_B.SharedValueObjectTwo"))
                          .simpleName(SimpleName.of("SharedValueObjectTwo"))
                          .description(Description.of(""))
                          .build());
            });
  }
}
