package io.candydoc.domain;

import candydoc.sample.wrong_bounded_context.NotAPackageInfo;
import io.candydoc.domain.exceptions.*;
import io.candydoc.domain.model.CoreConcept;
import io.candydoc.domain.model.ValueObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.reflections8.Reflections;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                .hasMessage("Empty parameters for 'packagesToScan'. Check your pom configuration");
    }

    ArgumentCaptor<List> initDocumentationGenerationTests(String packageToTest) throws IOException {
        ArgumentCaptor<List> resultCaptor = ArgumentCaptor.forClass(List.class);
        domain.generateDocumentation(GenerateDocumentation.builder()
                .packagesToScan(packageToTest)
                .build());
        verify(saveDocumentationPort, times(1)).save(resultCaptor.capture());
        return resultCaptor;
    }

    @Test
    void bounded_contexts_names_are_generated_in_the_documentation() throws DomainException, IOException {
        ArgumentCaptor<List> resultCaptor = initDocumentationGenerationTests("candydoc.sample.valid_bounded_contexts");
        Assertions.assertThat(resultCaptor.getValue()).extracting("name")
                .containsExactlyInAnyOrder("candydoc.sample.valid_bounded_contexts.bounded_context_one",
                        "candydoc.sample.valid_bounded_contexts.bounded_context_two");
    }

    @Test
    void bounded_contexts_descriptions_are_generated_in_the_documentation() throws DomainException, IOException {
        ArgumentCaptor<List> resultCaptor = initDocumentationGenerationTests("candydoc.sample.valid_bounded_contexts");
        Assertions.assertThat(resultCaptor.getValue()).extracting("description")
                .containsExactlyInAnyOrder("description of bounded context 1",
                        "description of bounded context 2");
    }

    @Test
    void generated_documentation_from_multiple_folder_is_saved() throws DomainException, IOException {
        ArgumentCaptor<List> resultCaptor = ArgumentCaptor.forClass(List.class);
        domain.generateDocumentation(GenerateDocumentation.builder()
                .packagesToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .packagesToScan("candydoc.sample.second_valid_bounded_contexts")
                .build());
        verify(saveDocumentationPort, times(1)).save(resultCaptor.capture());
        Assertions.assertThat(resultCaptor.getValue())
                .hasSize(2);
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

    @Test
    void number_of_extracted_core_concepts_is_correct() throws DomainException, IOException {
        ArgumentCaptor<List> resultCaptor = initDocumentationGenerationTests("candydoc.sample.valid_bounded_contexts");
        Assertions.assertThat(resultCaptor.getValue()).flatExtracting("coreConcepts")
                .hasSize(3);
    }

    @Test
    void number_of_extracted_value_objects_is_correct() throws DomainException, IOException {
        ArgumentCaptor<List> resultCaptor = initDocumentationGenerationTests("candydoc.sample.valid_bounded_contexts");
        Assertions.assertThat(resultCaptor.getValue()).flatExtracting("valueObjects")
                .hasSize(1);
    }

    @Test
    void core_concepts_are_generated_in_the_documentation() throws DomainException, IOException {
        ArgumentCaptor<List> resultCaptor = initDocumentationGenerationTests("candydoc.sample.valid_bounded_contexts");
        Assertions.assertThat(resultCaptor.getValue()).flatExtracting("coreConcepts")
                .containsExactlyInAnyOrder(CoreConcept.builder()
                                .name("name of core concept 1 of bounded context 1")
                                .description("description of core concept 1 of bounded context 1")
                                .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1")
                                .interactsWith(Set.of())
                                .build(),
                        CoreConcept.builder()
                                .name("name of core concept 2 of bounded context 1")
                                .description("description of core concept 2 of bounded context 1")
                                .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept2")
                                .interactsWith(Set.of())
                                .build(),
                        CoreConcept.builder()
                                .name("name of core concept 1 of bounded context 2")
                                .description("description of core concept 1 of bounded context 2")
                                .className("candydoc.sample.valid_bounded_contexts.bounded_context_two.CoreConcept1")
                                .interactsWith(Set.of())
                                .build());
    }

    @Test
    void value_objects_are_generated_in_the_documentation() throws DomainException, IOException {
        ArgumentCaptor<List> resultCaptor = initDocumentationGenerationTests("candydoc.sample.valid_bounded_contexts");
        Assertions.assertThat(resultCaptor.getValue()).flatExtracting("valueObjects")
                .containsExactlyInAnyOrder(ValueObject.builder()
                        .description("description of value object 1 of bounded context 1")
                        .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                        .build());
    }

    @Test
    void extract_core_concepts_from_project() {
        String boundedContextToScan = "candydoc.sample.bounded_context_for_core_concepts_tests";
        Assertions.assertThat(domain.extractCoreConcepts(boundedContextToScan))
                .contains(CoreConcept.builder()
                                .name("name of core concept 1")
                                .description("description of core concept 1")
                                .className("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                                .interactsWith(Set.of("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2"))
                                .build(),
                        CoreConcept.builder()
                                .name("name of core concept 2")
                                .description("description of core concept 2")
                                .className("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                                .interactsWith(Set.of("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1"))
                                .build());
    }

    @Test
    void extract_value_objects_from_project() {
        String boundedContextToScan = "candydoc.sample.bounded_context_for_value_objects_tests";
        Assertions.assertThat(domain.extractValueObjects(boundedContextToScan))
                .contains(ValueObject.builder()
                                .description("description of value object 1")
                                .className("candydoc.sample.bounded_context_for_value_objects_tests.ValueObject1")
                                .build(),
                        ValueObject.builder()
                                .description("description of value object 2")
                                .className("candydoc.sample.bounded_context_for_value_objects_tests.ValueObject2")
                                .build()
                );
    }

    @Test
    void throw_exception_when_core_concepts_are_duplicated_in_a_same_bounded_context() {
        String boundedContextToScan = "candydoc.sample.duplicated_core_concepts";
        Assertions.assertThatThrownBy(() -> domain.extractCoreConcepts(boundedContextToScan))
                .isInstanceOf(DocumentationGenerationFailed.class)
                .hasMessage("Multiple core concepts share the same name in a bounded context");
    }

    Set<Class<?>> coreConceptClassesInteractionsToTest() {
        Reflections reflections = new Reflections("candydoc.sample.bounded_context_for_core_concepts_tests");
        return reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.CoreConcept.class);
    }

    @Test
    void extract_interaction_from_variable() {
        Set<Class<?>> coreConceptClassesToTest = coreConceptClassesInteractionsToTest();
        Class<?> currentClass = coreConceptClassesToTest
                .stream().filter(coreConcept -> coreConcept.getName()
                        .equals("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1"))
                .collect(Collectors.toList()).get(0);
        Assertions.assertThat(domain.extractDDDInteractions(currentClass))
                .isEqualTo(Set.of("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2"));
    }

    @Test
    void extract_interaction_from_value_object() {
        Reflections reflections = new Reflections("candydoc.sample.bounded_context_for_value_objects_tests");
        Set<Class<?>> coreConceptClassesToTest = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.CoreConcept.class);
        Class<?> currentClass = coreConceptClassesToTest
                .stream().filter(coreConcept -> coreConcept.getName()
                        .equals("candydoc.sample.bounded_context_for_value_objects_tests.CoreConcept1"))
                .collect(Collectors.toList()).get(0);
        Assertions.assertThat(domain.extractDDDInteractions(currentClass))
                .isEqualTo(Set.of("candydoc.sample.bounded_context_for_value_objects_tests.ValueObject1",
                        "candydoc.sample.bounded_context_for_value_objects_tests.ValueObject2"));
    }

    @Test
    void extract_interaction_from_return_type() {
        Set<Class<?>> coreConceptClassesToTest = coreConceptClassesInteractionsToTest();
        Class<?> currentClass = coreConceptClassesToTest
                .stream().filter(coreConcept -> coreConcept.getName()
                        .equals("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2"))
                .collect(Collectors.toList()).get(0);
        Assertions.assertThat(domain.extractDDDInteractions(currentClass))
                .isEqualTo(Set.of("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1"));
    }

    @Test
    void extract_interaction_from_argument() {
        Set<Class<?>> coreConceptClassesToTest = coreConceptClassesInteractionsToTest();
        Class<?> currentClass = coreConceptClassesToTest
                .stream().filter(coreConcept -> coreConcept.getName()
                        .equals("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept3"))
                .collect(Collectors.toList()).get(0);
        Assertions.assertThat(domain.extractDDDInteractions(currentClass))
                .isEqualTo(Set.of("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1"));
    }

    @Test
    void core_concepts_interactions_extraction_does_not_have_any_duplicates() {
        Set<Class<?>> coreConceptClassesToTest = coreConceptClassesInteractionsToTest();
        Class<?> currentClass = coreConceptClassesToTest
                .stream().filter(coreConcept -> coreConcept.getName()
                        .equals("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConceptWithDuplicates"))
                .collect(Collectors.toList()).get(0);
        Assertions.assertThat(domain.extractDDDInteractions(currentClass))
                .isEqualTo(Set.of("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1"));
    }

    @Test
    void get_classes_annotated_with_ddd_annotations() {
        Reflections reflections = new Reflections("candydoc.sample.bounded_context_for_value_objects_tests");
        Set<Class<?>> coreConceptClassesToTest = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.CoreConcept.class);
        Class<?> currentClass = coreConceptClassesToTest
                .stream().filter(coreConcept -> coreConcept.getName()
                        .equals("candydoc.sample.bounded_context_for_value_objects_tests.CoreConcept1"))
                .collect(Collectors.toList()).get(0);
        Assertions.assertThat(domain.getClassesAnnotatedWithDDDAnnotations(currentClass)).isEqualTo(Set.of(io.candydoc.domain.annotations.CoreConcept.class));
    }

    @Test
    void value_object_is_not_following_ddd() {
        String packagesToScan = "candydoc.sample.bounded_context_for_wrong_usage_of_value_objects";
        Assertions.assertThatThrownBy(() -> domain.extractValueObjects(packagesToScan))
                .isInstanceOf(WrongUsageOfValueObject.class)
                .hasMessage("Value object should use primitive types only: [class candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.ValueObject]");
    }
}