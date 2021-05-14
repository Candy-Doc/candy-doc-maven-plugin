package io.candydoc.domain;

import io.candydoc.domain.command.ExtractDDDConcept;
import io.candydoc.domain.events.*;
import io.candydoc.domain.exceptions.DocumentationGenerationFailed;
import io.candydoc.domain.exceptions.DomainException;
import io.candydoc.domain.exceptions.NoBoundedContextFound;
import io.candydoc.domain.extractor.DDDConceptExtractor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

class DomainTest {

    private Domain domain;
    private SaveDocumentationPort saveDocumentationPort;
    private DDDConceptExtractor DDDConceptExtractor;

    @BeforeEach
    public void setUp() {
        saveDocumentationPort = mock(SaveDocumentationPort.class);
        domain = new Domain(saveDocumentationPort);
        DDDConceptExtractor = new DDDConceptExtractor();
    }

    @Test
    void package_to_scan_is_not_provided() {
        //given
        List<String> givenPackages = List.of();
        //then
        Assertions.assertThatThrownBy(() -> domain.generateDocumentation(ExtractDDDConcept.builder()
                .packagesToScan(givenPackages)
                .build()))
                .isInstanceOf(DocumentationGenerationFailed.class)
                .hasMessage("Missing parameters for 'packageToScan'. Check your pom configuration.");
    }

    @Test
    void package_to_scan_report_empty_string() {
        //given
        String givenPackage = "";
        //then
        Assertions.assertThatThrownBy(() -> domain.generateDocumentation(ExtractDDDConcept.builder()
                .packagesToScan(givenPackage)
                .build()))
                .isInstanceOf(DocumentationGenerationFailed.class)
                .hasMessage("Empty parameters for 'packagesToScan'. Check your pom configuration");
    }

    @Test
    void generated_documentation_from_multiple_folder_is_saved() throws DomainException, IOException{
        //given
        ArgumentCaptor<List> resultCaptor = ArgumentCaptor.forClass(List.class);
        //when
        domain.generateDocumentation(ExtractDDDConcept.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .packagesToScan("candydoc.sample.second_valid_bounded_contexts")
                .build());
        verify(saveDocumentationPort, times(1)).save(resultCaptor.capture());
        //then
        Assertions.assertThat(resultCaptor.getValue())
                .hasSize(8);
    }

    @Test
    void package_to_scan_is_not_following_ddd() {
        //given
        List<String> packagesToScan = List.of("wrong.package.to.scan");
        //then
        Assertions.assertThatThrownBy(() -> domain.generateDocumentation(ExtractDDDConcept.builder().packagesToScan(packagesToScan).build()))
                .isInstanceOf(NoBoundedContextFound.class)
                .hasMessage("No bounded context has been found in the package : 'wrong.package.to.scan'.");
    }

    @Test
    void bounded_context_found_event_are_generated() {
        //given
        ExtractDDDConcept command = ExtractDDDConcept.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(BoundedContextFound.builder()
                                .name("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                                .description("description of bounded context 1")
                                .build(),
                        BoundedContextFound.builder()
                                .name("candydoc.sample.valid_bounded_contexts.bounded_context_two")
                                .description("description of bounded context 2")
                                .build());
    }

    @Test
    void core_concept_found_event_are_generated() {
        //given
        ExtractDDDConcept command = ExtractDDDConcept.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(CoreConceptFound.builder()
                        .name("name of core concept 1 of bounded context 1")
                        .description("description of core concept 1 of bounded context 1")
                        .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1")
                        .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .build(),
                        CoreConceptFound.builder().name("name of core concept 2 of bounded context 1")
                        .description("description of core concept 2 of bounded context 1")
                        .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept2")
                        .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .build());
    }

    @Test
    void value_object_found_event_are_generated() {
        //given
        ExtractDDDConcept command = ExtractDDDConcept.builder().packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one").build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(ValueObjectFound.builder()
                        .description("description of value object 1 of bounded context 1")
                        .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                        .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .build());
    }

    @Test
    void wrong_usage_of_value_object_found_is_generated() {
        //given
        ExtractDDDConcept command = ExtractDDDConcept.builder()
                .packagesToScan("candydoc.sample.bounded_context_for_wrong_usage_of_value_objects")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(WrongUsageOfValueObjectFound.builder()
                        .valueObject("candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.ValueObject")
                        .usageError("Value Object should only contain primitive type")
                        .build());
    }

    @Test
    void domain_event_found_are_generated(){
        //given
        ExtractDDDConcept command = ExtractDDDConcept.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(DomainEventFound.builder()
                        .description("domain event 1 of boundedcontext 1")
                        .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
                        .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .build());
    }

    @Test
    void domain_command_found_is_generated() {
        //given
        ExtractDDDConcept command = ExtractDDDConcept.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(DomainCommandFound.builder()
                        .description("Domain Command for Bounded context 1")
                        .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
                        .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .build());
    }

    @Test
    void domain_command_interaction_between_concept_found() {
        //given
        ExtractDDDConcept command = ExtractDDDConcept.builder()
                .packagesToScan("candydoc.sample.bounded_context_for_value_objects_tests")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(InteractionBetweenConceptFound.builder()
                        .from("candydoc.sample.bounded_context_for_value_objects_tests.CoreConcept1")
                        .with("candydoc.sample.bounded_context_for_value_objects_tests.ValueObject1")
                        .build());
    }
}