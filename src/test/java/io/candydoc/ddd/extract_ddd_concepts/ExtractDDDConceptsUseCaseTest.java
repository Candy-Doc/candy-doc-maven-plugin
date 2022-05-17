package io.candydoc.ddd.extract_ddd_concepts;

import static org.mockito.Mockito.*;

import io.candydoc.ddd.Command;
import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.aggregate.AggregateFound;
import io.candydoc.ddd.annotations.DDDKeywords;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.bounded_context.BoundedContextFound;
import io.candydoc.ddd.bounded_context.NoBoundedContextFound;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.core_concept.CoreConceptFound;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_command.DomainCommandFound;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.domain_event.DomainEventFound;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.value_object.ValueObject;
import io.candydoc.ddd.value_object.ValueObjectFound;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@Slf4j
class ExtractDDDConceptsUseCaseTest {

  private ExtractDDDConceptsUseCase extractDDDConceptsUseCase;
  private SaveDocumentationPort saveDocumentationPort;
  private DDDConceptsExtractionService dddConceptsExtractionService;

  private ResultCaptor<List<Event>> extractionCaptor = new ResultCaptor<>();

  @BeforeEach
  public void setUp() {
    saveDocumentationPort = mock(SaveDocumentationPort.class);
    DDDConceptFinder conceptFinder =
        new DDDConceptFinder() {
          @Override
          public Set<DDDConcept> findDDDConcepts() {
            // Todo : Sort it so it is a bit more readable
            return Set.of(
                BoundedContext.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.duplicated_core_concepts.package-info"))
                    .simpleName(SimpleName.of("duplicate core concept"))
                    .packageName(PackageName.of("io.candydoc.sample.duplicated_core_concepts"))
                    .description(Description.of("bounded context for duplicated core concept"))
                    .build(),
                ValueObject.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.EnumValueObject"))
                    .simpleName(SimpleName.of("EnumValueObject"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .description(Description.of("My enum value object description"))
                    .build(),
                DomainEvent.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainEvent1"))
                    .simpleName(SimpleName.of("DomainEvent1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package"))
                    .description(Description.of("domain event 1 of boundedcontext 1"))
                    .build(),
                CoreConcept.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.CoreConcept1"))
                    .simpleName(SimpleName.of("CoreConcept1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .build(),
                DomainEvent.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.DomainEvent2"))
                    .simpleName(SimpleName.of("DomainEvent2"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .description(Description.of("My domain event description 2"))
                    .build(),
                DomainCommand.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainCommand1"))
                    .simpleName(SimpleName.of("DomainCommand1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package"))
                    .description(Description.of("Domain Command for Bounded context 1"))
                    .build(),
                BoundedContext.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.package-info"))
                    .simpleName(SimpleName.of("bounded for wrong usage"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects"))
                    .description(Description.of("bounded context for wrong usage of value object"))
                    .build(),
                Aggregate.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.Aggregate1"))
                    .simpleName(SimpleName.of("Aggregate1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .build(),
                DomainCommand.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.DomainCommand1"))
                    .simpleName(SimpleName.of("DomainCommand1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .build(),
                CoreConcept.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.duplicated_core_concepts.another_package.CoreConcept1"))
                    .simpleName(SimpleName.of("duplicated core concept"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.duplicated_core_concepts.another_package"))
                    .description(Description.of("duplicated core concept 1"))
                    .build(),
                Aggregate.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.Aggregate1"))
                    .simpleName(SimpleName.of("aggregate 1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package"))
                    .description(Description.of("Aggregate for Bounded context 1"))
                    .build(),
                ValueObject.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.sub_package.ValueObject"))
                    .simpleName(SimpleName.of("ValueObject"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.sub_package"))
                    .description(
                        Description.of(
                            "value object interacting withFullName something it shouldn't"))
                    .build(),
                CoreConcept.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.duplicated_core_concepts.sub_package.CoreConcept2"))
                    .simpleName(SimpleName.of("duplicated core concept"))
                    .packageName(
                        PackageName.of("io.candydoc.sample.duplicated_core_concepts.sub_package"))
                    .description(Description.of("duplicated core concept 2"))
                    .build(),
                DomainEvent.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.DomainEvent1"))
                    .simpleName(SimpleName.of("DomainEvent1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .build(),
                DomainCommand.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.DomainCommand2"))
                    .simpleName(SimpleName.of("DomainCommand2"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .description(Description.of("My domain command description 2"))
                    .build(),
                BoundedContext.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_two.package-info"))
                    .simpleName(SimpleName.of("bounded_context_two"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_two"))
                    .description(Description.of("description of bounded context 2"))
                    .build(),
                BoundedContext.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.second_valid_bounded_contexts.package-info"))
                    .simpleName(SimpleName.of("second valid bounded context"))
                    .packageName(PackageName.of("io.candydoc.sample.second_valid_bounded_contexts"))
                    .description(Description.of("second valid bounded contexts"))
                    .build(),
                CoreConcept.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.CoreConcept2"))
                    .simpleName(SimpleName.of("My core concept 2"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .description(Description.of("My core concept description 2"))
                    .build(),
                CoreConcept.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept2"))
                    .simpleName(SimpleName.of("name of core concept 2 of bounded context 1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package"))
                    .description(
                        Description.of("description of core concept 2 of bounded context 1"))
                    .build(),
                BoundedContext.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info"))
                    .simpleName(SimpleName.of("bounded_context_one"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one"))
                    .description(Description.of("description of bounded context 1"))
                    .build(),
                CoreConcept.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept1"))
                    .simpleName(SimpleName.of("name of core concept 1 of bounded context 1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package"))
                    .description(
                        Description.of("description of core concept 1 of bounded context 1"))
                    .build(),
                BoundedContext.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.package-info"))
                    .simpleName(SimpleName.of("package-info"))
                    .packageName(
                        PackageName.of("io.candydoc.sample.concepts_with_deducted_annotations"))
                    .build(),
                ValueObject.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.ValueObject1"))
                    .simpleName(SimpleName.of("ValueObject1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .build(),
                CoreConcept.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.EnumCoreConcept"))
                    .simpleName(SimpleName.of("My enum core concept"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .description(Description.of("My enum core concept description"))
                    .build(),
                ValueObject.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.ValueObject1"))
                    .simpleName(SimpleName.of("ValueObject1"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package"))
                    .description(
                        Description.of("description of value object 1 of bounded context 1"))
                    .build(),
                CoreConcept.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.sub_package.CoreConcept"))
                    .simpleName(SimpleName.of("core concept for wrong usage of value object"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.sub_package"))
                    .description(
                        Description.of(
                            "description of core concept for wrong usage of value object"))
                    .build(),
                Aggregate.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.Aggregate2"))
                    .simpleName(SimpleName.of("My aggregate 2"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .description(Description.of("My aggregate description 2"))
                    .build(),
                ValueObject.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.ValueObject2"))
                    .simpleName(SimpleName.of("ValueObject2"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.concepts_with_deducted_annotations.sub_package"))
                    .description(Description.of("My value object description 2"))
                    .build(),
                CoreConcept.builder()
                    .canonicalName(
                        CanonicalName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_two.sub_package.CoreConcept1"))
                    .simpleName(SimpleName.of("name of core concept 1 of bounded context 2"))
                    .packageName(
                        PackageName.of(
                            "io.candydoc.sample.valid_bounded_contexts.bounded_context_two.sub_package"))
                    .description(
                        Description.of("description of core concept 1 of bounded context 2"))
                    .build());
          }

          @Override
          public Set<Aggregate> findAggregates(String packageToScan) {
            return findDDDConcepts().stream()
                .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
                .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(Aggregate.class))
                .map(Aggregate.class::cast)
                .collect(Collectors.toUnmodifiableSet());
          }

          @Override
          public Set<BoundedContext> findBoundedContexts(String packageToScan) {
            return findDDDConcepts().stream()
                .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
                .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(BoundedContext.class))
                .map(BoundedContext.class::cast)
                .collect(Collectors.toUnmodifiableSet());
          }

          @Override
          public Set<CoreConcept> findCoreConcepts(String packageToScan) {
            return findDDDConcepts().stream()
                .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
                .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(CoreConcept.class))
                .map(CoreConcept.class::cast)
                .collect(Collectors.toUnmodifiableSet());
          }

          @Override
          public Set<DomainCommand> findDomainCommands(String packageToScan) {
            return findDDDConcepts().stream()
                .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
                .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(DomainCommand.class))
                .map(DomainCommand.class::cast)
                .collect(Collectors.toUnmodifiableSet());
          }

          @Override
          public Set<DomainEvent> findDomainEvents(String packageToScan) {
            return findDDDConcepts().stream()
                .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
                .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(DomainEvent.class))
                .map(DomainEvent.class::cast)
                .collect(Collectors.toUnmodifiableSet());
          }

          @Override
          public Set<ValueObject> findValueObjects(String packageToScan) {
            return findDDDConcepts().stream()
                .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
                .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(ValueObject.class))
                .map(ValueObject.class::cast)
                .collect(Collectors.toUnmodifiableSet());
          }

          @Override
          public Set<Interaction> findInteractionsWith(CanonicalName conceptName) {
            return conceptsInteractingWith(conceptName).stream()
                .map(canonicalName -> Interaction.with(canonicalName.value()))
                .collect(Collectors.toUnmodifiableSet());
          }

          @Override
          public DDDConcept findConcept(CanonicalName conceptName) {
            return findDDDConcepts().stream()
                .filter(concept -> concept.getCanonicalName().equals(conceptName))
                .findFirst()
                .orElseThrow();
          }

          // Todo : Write manually concepts interacting with like in findConcepts
          private Set<CanonicalName> conceptsInteractingWith(CanonicalName conceptName) {
            try {
              Class<?> clazz =
                  Class.forName(
                      conceptName.value(), false, Thread.currentThread().getContextClassLoader());
              Set<Class<?>> interactions = new HashSet<>();

              Arrays.stream(clazz.getDeclaredFields())
                  .map(Field::getType)
                  .forEach(interactions::add);

              Arrays.stream(clazz.getDeclaredMethods())
                  .forEach(
                      method -> {
                        interactions.add(method.getReturnType());
                        interactions.addAll(Set.of(method.getParameterTypes()));
                      });

              return interactions.stream()
                  .filter(this::isDDDAnnotated)
                  .map(this::toCanonicalName)
                  .collect(Collectors.toUnmodifiableSet());
            } catch (ClassNotFoundException e) {
              throw new ExtractionException(e.getMessage());
            }
          }

          private CanonicalName toCanonicalName(Class<?> clazz) {
            return CanonicalName.of(clazz.getCanonicalName());
          }

          private boolean isDDDAnnotated(Class<?> clazz) {
            Set<Annotation> annotations = Set.of(clazz.getAnnotations());
            return annotations.stream()
                .anyMatch(annotation -> DDDKeywords.KEYWORDS.contains(annotation.annotationType()));
          }
        };

    dddConceptsExtractionService = spy(new DDDConceptsExtractionService(conceptFinder));

    extractDDDConceptsUseCase =
        new ExtractDDDConceptsUseCase(dddConceptsExtractionService, saveDocumentationPort);

    doAnswer(extractionCaptor).when(dddConceptsExtractionService).extract(any(Command.class));
  }

  @Test
  void package_to_scan_is_not_provided() {
    // given
    List<String> givenPackages = List.of();

    ExtractDDDConcepts command = ExtractDDDConcepts.builder().packagesToScan(givenPackages).build();

    // when then
    Assertions.assertThatThrownBy(() -> extractDDDConceptsUseCase.execute(command))
        .isInstanceOf(DocumentationGenerationFailed.class)
        .hasMessage("Missing parameters for 'packageToScan'. Check your pom configuration.");
  }

  @Test
  void package_to_scan_report_empty_string() {
    // given
    ExtractDDDConcepts command = ExtractDDDConcepts.builder().packageToScan("").build();

    // when then
    Assertions.assertThatThrownBy(() -> extractDDDConceptsUseCase.execute(command))
        .isInstanceOf(DocumentationGenerationFailed.class)
        .hasMessage("Empty parameters for 'packagesToScan'. Check your pom configuration");
  }

  @Test
  void generated_documentation_from_multiple_packages() throws ExtractionException, IOException {
    // given
    ArgumentCaptor<List<Event>> resultCaptor = ArgumentCaptor.forClass(List.class);

    // when
    extractDDDConceptsUseCase.execute(
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .packageToScan("io.candydoc.sample.second_valid_bounded_contexts")
            .build());

    // then
    verify(saveDocumentationPort, times(1)).save(resultCaptor.capture());

    List<Event> occurredGenerationEvents = resultCaptor.getValue();

    Assertions.assertThat(occurredGenerationEvents).isNotEmpty();
  }

  @Test
  void package_to_scan_is_not_following_ddd() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder().packageToScan("wrong.package.to.scan").build();

    // then
    Assertions.assertThatThrownBy(() -> extractDDDConceptsUseCase.execute(command))
        .isInstanceOf(NoBoundedContextFound.class)
        .hasMessage("No bounded context has been found in the package : 'wrong.package.to.scan'.");
  }

  @Test
  void find_bounded_contexts_inside_given_packages() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            BoundedContextFound.builder()
                .name("bounded_context_one")
                .description("description of bounded context 1")
                .packageName("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build(),
            BoundedContextFound.builder()
                .name("bounded_context_two")
                .packageName("io.candydoc.sample.valid_bounded_contexts.bounded_context_two")
                .description("description of bounded context 2")
                .build());
  }

  @Test
  void find_core_concepts_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            CoreConceptFound.builder()
                .simpleName("name of core concept 1 of bounded context 1")
                .description("description of core concept 1 of bounded context 1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build(),
            CoreConceptFound.builder()
                .simpleName("name of core concept 2 of bounded context 1")
                .description("description of core concept 2 of bounded context 1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept2")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_value_objects_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            ValueObjectFound.builder()
                .description("description of value object 1 of bounded context 1")
                .simpleName("ValueObject1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.ValueObject1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void value_object_should_only_contain_primitive_types() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            ConceptRuleViolated.builder()
                .conceptName(
                    "io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.sub_package.ValueObject")
                .reason("Value Object should only contain primitive types")
                .build());
  }

  @Test
  void find_domain_events_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            DomainEventFound.builder()
                .description("domain event 1 of boundedcontext 1")
                .simpleName("DomainEvent1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainEvent1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_domain_commands_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            DomainCommandFound.builder()
                .description("Domain Command for Bounded context 1")
                .simpleName("DomainCommand1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainCommand1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_aggregates_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            AggregateFound.builder()
                .simpleName("aggregate 1")
                .description("Aggregate for Bounded context 1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.Aggregate1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_interaction_between_two_different_concepts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            InteractionBetweenConceptFound.builder()
                .from(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept1")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.ValueObject1")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept1")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept2")
                .build());
  }

  public class ResultCaptor<T> implements Answer {
    private T result = null;

    public T getResult() {
      return result;
    }

    @Override
    public T answer(InvocationOnMock invocationOnMock) throws Throwable {
      result = (T) invocationOnMock.callRealMethod();
      return result;
    }
  }
}
