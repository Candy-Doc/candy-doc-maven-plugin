package io.candydoc.ddd.extract_domain_models;

import io.candydoc.ddd.model.PackageName;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExtractionTest {

    @Mock
    DomainModelFinder domainModelFinder;
    @Mock
    UbiquitousLanguageRepository ubiquitousLanguageRepository;
    @InjectMocks
    ExtractDomainModelsUseCase useCase;

    @Test
    public void packages_to_scan_are_not_provided() {
        // given
        ExtractDomainModels command = ExtractDomainModels.builder()
            .packagesToScan(List.of())
            .build();

        // when
        ThrowableAssert.ThrowingCallable callable = () -> useCase.execute(command);

        // then
        assertThatThrownBy(callable)
            .isInstanceOf(ExtractionException.class)
            .isInstanceOf(PackageToScanMissing.class)
            .hasMessage("At least one 'packageToScan' must be provided. Check your pom configuration.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t  ", "  \t "})
    public void package_to_scan_can_not_be_blank(String blankPackage) {
        // given
        ExtractDomainModels command = ExtractDomainModels.builder()
            .packageToScan(blankPackage)
            .build();

        // when
        ThrowableAssert.ThrowingCallable callable = () -> useCase.execute(command);

        // then
        assertThatThrownBy(callable)
            .isInstanceOf(ExtractionException.class)
            .isInstanceOf(PackageToScanMissing.class)
            .hasMessage("Blank packageToScan not allowed for 'packagesToScan'. Check your pom configuration.");
    }

    @Test
    public void no_ubiquitous_languages_to_save() {
        // given
        when(domainModelFinder.findDomainModels(any()))
            .thenReturn(Set.of());

        ExtractDomainModels command = ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample")
            .build();

        // when
        useCase.execute(command);

        // then
        verify(ubiquitousLanguageRepository, never())
            .saveAll(any());

        verify(domainModelFinder)
            .findDomainModels(PackageName.of("io.candydoc.ddd.sample"));
    }

}
