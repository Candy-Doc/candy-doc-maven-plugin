package io.candydoc.domain;

import candydoc.sample.wrong_bounded_context.NotAPackageInfo;
import io.candydoc.domain.exceptions.DocumentationGenerationFailed;
import io.candydoc.domain.exceptions.DomainException;
import io.candydoc.domain.exceptions.NoBoundedContextFound;
import io.candydoc.domain.exceptions.WrongUsageOfBoundedContext;
import io.candydoc.domain.model.BoundedContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

class DomainTest {

    private Domain domain;

    private SaveDocumentationPort saveDocumentationPort;


    @BeforeEach
    public void setUp() {
        saveDocumentationPort = mock(SaveDocumentationPort.class);
        domain = new Domain(saveDocumentationPort);
    }

    @Test
    void package_to_scan_is_not_provided() {
        Assertions.assertThatThrownBy(() -> domain.generateDocumentation(GenerateDocumentation.builder()
                .packagesToScan(List.of())
                .build()))
                .isInstanceOf(DocumentationGenerationFailed.class)
                .hasMessage("Missing parameters for 'packageToScan'. Check your pom configuration.");
    }

    @Test
    void package_to_scan_report_empty_string() {
        Assertions.assertThatThrownBy(() -> domain.generateDocumentation(GenerateDocumentation.builder()
                .packagesToScan("")
                .build()))
                .isInstanceOf(DocumentationGenerationFailed.class)
                .hasMessage("empty parameters for 'packagesToScan'. Check your pom configuation");
    }

    @Test
    void generated_documentation_is_saved() throws DomainException, IOException {
        ArgumentCaptor<List> resultCaptor = ArgumentCaptor.forClass(List.class);
        domain.generateDocumentation(GenerateDocumentation.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts")
                .build());
        verify(saveDocumentationPort, times(1)).save(resultCaptor.capture());
        Assertions.assertThat(resultCaptor.getValue()).containsExactlyInAnyOrder(BoundedContext.builder()
                        .name("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .description("test package 1")
                        .build(),
                BoundedContext.builder()
                        .name("candydoc.sample.valid_bounded_contexts.bounded_context_two")
                        .description("test package 2")
                        .build());
    }

    @Test
    void generated_documentation_from_mutiple_folder_is_saved() throws DomainException, IOException {
        ArgumentCaptor<List> resultCaptor = ArgumentCaptor.forClass(List.class);
        domain.generateDocumentation(GenerateDocumentation.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .packagesToScan("candydoc.sample.second_valid_bounded_contexts")
                .build());
        verify(saveDocumentationPort, times(1)).save(resultCaptor.capture());
        Assertions.assertThat(resultCaptor.getValue()).containsExactlyInAnyOrder(BoundedContext.builder()
                        .name("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .description("test package 1")
                        .build(),
                BoundedContext.builder()
                        .name("candydoc.sample.second_valid_bounded_contexts")
                        .description("second valid bounded contexts")
                        .build());
    }

    @Test
    void package_to_scan_is_not_following_ddd() {
        List<String> packagesToScan = List.of("wrong.package.to.scan");
        Assertions.assertThatThrownBy(() -> domain.extractBoundedContexts(packagesToScan))
                .isInstanceOf(NoBoundedContextFound.class)
                .hasMessage("No bounded context has been found in the package : 'wrong.package.to.scan'.");
    }

    @Test
    void bounded_context_annotation_must_only_exist_on_package_info() {
        GenerateDocumentation command = GenerateDocumentation.builder()
                .packagesToScan(List.of("candydoc.sample.wrong_bounded_context"))
                .build();
        Assertions.assertThatThrownBy(() -> domain.generateDocumentation(command))
                .isInstanceOf(WrongUsageOfBoundedContext.class)
                .extracting("wrongClasses")
                .isEqualTo(List.of(NotAPackageInfo.class));

    }
}