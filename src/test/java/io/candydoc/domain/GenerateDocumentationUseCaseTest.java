package io.candydoc.domain;

import io.candydoc.domain.command.ExtractDDDConcepts;
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

class GenerateDocumentationUseCaseTest {

    private GenerateDocumentationUseCase generateDocumentationUseCase;
    private SaveDocumentationPort saveDocumentationPort;
    private DDDConceptExtractor DDDConceptExtractor;

    @BeforeEach
    public void setUp() {
        saveDocumentationPort = mock(SaveDocumentationPort.class);
        generateDocumentationUseCase = new GenerateDocumentationUseCase(saveDocumentationPort);
        DDDConceptExtractor = new DDDConceptExtractor();
    }

    @Test
    void package_to_scan_is_not_provided() {
        //given
        List<String> givenPackages = List.of();
        //then
        Assertions.assertThatThrownBy(() -> generateDocumentationUseCase.execute(ExtractDDDConcepts.builder()
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
        Assertions.assertThatThrownBy(() -> generateDocumentationUseCase.execute(ExtractDDDConcepts.builder()
                .packagesToScan(givenPackage)
                .build()))
                .isInstanceOf(DocumentationGenerationFailed.class)
                .hasMessage("Empty parameters for 'packagesToScan'. Check your pom configuration");
    }

    @Test
    void generated_documentation_from_multiple_folder_is_saved() throws DomainException, IOException {
        //given
        ArgumentCaptor<List> resultCaptor = ArgumentCaptor.forClass(List.class);
        //when
        generateDocumentationUseCase.execute(ExtractDDDConcepts.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .packagesToScan("candydoc.sample.second_valid_bounded_contexts")
                .build());
        verify(saveDocumentationPort, times(1)).save(resultCaptor.capture());
        //then
        Assertions.assertThat(resultCaptor.getValue())
                .hasSize(10);
    }

    @Test
    void package_to_scan_is_not_following_ddd() {
        //given
        List<String> packagesToScan = List.of("wrong.package.to.scan");
        //then
        Assertions.assertThatThrownBy(() -> generateDocumentationUseCase.execute(ExtractDDDConcepts.builder().packagesToScan(packagesToScan).build()))
                .isInstanceOf(NoBoundedContextFound.class)
                .hasMessage("No bounded context has been found in the package : 'wrong.package.to.scan'.");
    }

    @Test
    void bounded_context_found_event_are_generated() {
        //given
        ExtractDDDConcepts command = ExtractDDDConcepts.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(BoundedContextFound.builder()
                                .name("bounded_context_one")
                                .description("description of bounded context 1")
                                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                                .build(),
                        BoundedContextFound.builder()
                                .name("bounded_context_two")
                                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_two")
                                .description("description of bounded context 2")
                                .build());
    }

    @Test
    void core_concept_found_event_are_generated() {
        //given
        ExtractDDDConcepts command = ExtractDDDConcepts.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(CoreConceptFound.builder()
                                .name("name of core concept 1 of bounded context 1")
                                .description("description of core concept 1 of bounded context 1")
                                .className("CoreConcept1")
                                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1")
                                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                                .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                                .build(),
                        CoreConceptFound.builder().name("name of core concept 2 of bounded context 1")
                                .description("description of core concept 2 of bounded context 1")
                                .className("CoreConcept2")
                                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept2")
                                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                                .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                                .build());
    }

    @Test
    void value_object_found_event_are_generated() {
        //given
        ExtractDDDConcepts command = ExtractDDDConcepts.builder().packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one").build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(ValueObjectFound.builder()
                        .description("description of value object 1 of bounded context 1")
                        .className("ValueObject1")
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                        .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .build());
    }

    @Test
    void wrong_usage_of_value_object_found_is_generated() {
        //given
        ExtractDDDConcepts command = ExtractDDDConcepts.builder()
                .packagesToScan("candydoc.sample.bounded_context_for_wrong_usage_of_value_objects")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(ConceptRuleViolated.builder()
                        .conceptFullName("candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.ValueObject")
                        .reason("Value Object should only contain primitive types")
                        .build());
    }

    @Test
    void domain_event_found_are_generated() {
        //given
        ExtractDDDConcepts command = ExtractDDDConcepts.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(DomainEventFound.builder()
                        .description("domain event 1 of boundedcontext 1")
                        .className("DomainEvent1")
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
                        .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .build());
    }

    @Test
    void domain_command_found_is_generated() {
        //given
        ExtractDDDConcepts command = ExtractDDDConcepts.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(DomainCommandFound.builder()
                        .description("Domain Command for Bounded context 1")
                        .className("DomainCommand1")
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
                        .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .build());
    }

    @Test
    void aggregate_found_is_generated() {
        //given
        ExtractDDDConcepts command = ExtractDDDConcepts.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(AggregateFound.builder()
                        .name("aggregate 1")
                        .description("Aggregate for Bounded context 1")
                        .className("Aggregate1")
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.Aggregate1")
                        .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                        .build());
    }

    @Test
    void domain_command_interaction_between_concept_found() {
        //given
        ExtractDDDConcepts command = ExtractDDDConcepts.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts")
                .build();
        //when
        List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
        //then
        Assertions.assertThat(actualEvents)
                .contains(InteractionBetweenConceptFound.builder()
                        .from("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1")
                        .withFullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                        .build());
    }
}
