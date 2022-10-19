package io.candydoc.ddd.extract_domain_models;

import io.candydoc.ddd.annotations.DDDKeywords;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;
import org.reflections8.util.ClasspathHelper;
import org.reflections8.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ReflectionsDomainModelFinder implements DomainModelFinder {

    public static final Map<Class<? extends Annotation>, Function<Class<?>, DomainModel>>
        ANNOTATION_PROCESSORS =
        Map.of(
            io.candydoc.ddd.annotations.Aggregate.class,
            ReflectionsDomainModelFinder::toAggregate,
            io.candydoc.ddd.annotations.BoundedContext.class,
            ReflectionsDomainModelFinder::toBoundedContext,
            io.candydoc.ddd.annotations.DomainCommand.class,
            ReflectionsDomainModelFinder::toDomainCommand,
            io.candydoc.ddd.annotations.DomainEvent.class,
            ReflectionsDomainModelFinder::toDomainEvent,
            io.candydoc.ddd.annotations.SharedKernel.class,
            ReflectionsDomainModelFinder::toSharedKernel,
            io.candydoc.ddd.annotations.ValueObject.class,
            ReflectionsDomainModelFinder::toValueObject);

    private static Set<DomainModel> foundConcepts;
    private final Reflections reflections;

    public ReflectionsDomainModelFinder(List<String> packagesToScan) {
        reflections =
            new Reflections(
                new ConfigurationBuilder()
                    .setUrls(
                        packagesToScan.stream()
                            .map(ClasspathHelper::forPackage)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toUnmodifiableSet())));
    }

    @Override
    public Set<DomainModel> findDomainModels(PackageName packageToScan) {
        if (foundConcepts == null) {
            foundConcepts =
                DDDKeywords.KEYWORDS.stream()
                    .flatMap(annotation -> {
                        Function<Class<?>, DomainModel> processor = ANNOTATION_PROCESSORS.get(annotation);

                        return reflections.getTypesAnnotatedWith(annotation).stream()
                            .filter(clazz -> !clazz.isAnonymousClass())
                            .map(processor);
                    })
                    .collect(Collectors.toUnmodifiableSet());
        }
        return foundConcepts.stream()
            .filter(dddConcept -> dddConcept.packageName().startsWith(packageToScan))
            .collect(Collectors.toUnmodifiableSet());
    }

    private static Aggregate toAggregate(Class<?> clazz) {
        String simpleName = clazz.getAnnotation(io.candydoc.ddd.annotations.Aggregate.class).name();
        if (simpleName.isBlank()) {
            simpleName = clazz.getSimpleName();
        }

        String description = clazz.getAnnotation(io.candydoc.ddd.annotations.Aggregate.class).description();

        return Aggregate.builder()
            .canonicalName(toCanonicalName(clazz))
            .simpleName(SimpleName.of(simpleName))
            .packageName(toPackageName(clazz))
            .description(Description.of(description))
            .dependencies(dependenciesOf(clazz))
            .build();
    }

    private static BoundedContext toBoundedContext(Class<?> clazz) {
        String simpleName = clazz.getAnnotation(io.candydoc.ddd.annotations.BoundedContext.class).name();
        if (simpleName.isBlank()) {
            String packageName = clazz.getPackageName();
            String[] packageParts = packageName.split("\\.");
            simpleName = packageParts[packageParts.length - 1];
        }

        String description = clazz.getAnnotation(io.candydoc.ddd.annotations.BoundedContext.class).description();

        return BoundedContext.builder()
            .canonicalName(toCanonicalName(clazz))
            .simpleName(SimpleName.of(simpleName))
            .packageName(toPackageName(clazz))
            .description(Description.of(description))
            .dependencies(dependenciesOf(clazz))
            .build();
    }

    private static DomainCommand toDomainCommand(Class<?> clazz) {
        String description = clazz.getAnnotation(io.candydoc.ddd.annotations.DomainCommand.class).description();

        return DomainCommand.builder()
            .canonicalName(toCanonicalName(clazz))
            .simpleName(SimpleName.of(clazz.getSimpleName()))
            .packageName(toPackageName(clazz))
            .description(Description.of(description))
            .dependencies(dependenciesOf(clazz))
            .build();
    }

    private static DomainEvent toDomainEvent(Class<?> clazz) {
        String description = clazz.getAnnotation(io.candydoc.ddd.annotations.DomainEvent.class).description();

        return DomainEvent.builder()
            .canonicalName(toCanonicalName(clazz))
            .simpleName(SimpleName.of(clazz.getSimpleName()))
            .packageName(toPackageName(clazz))
            .description(Description.of(description))
            .dependencies(dependenciesOf(clazz))
            .build();
    }

    private static DomainModel toSharedKernel(Class<?> clazz) {
        String simpleName = clazz.getAnnotation(io.candydoc.ddd.annotations.SharedKernel.class).name();
        if (simpleName.isBlank()) {
            String packageName = clazz.getPackageName();
            String[] packageParts = packageName.split("\\.");
            simpleName = packageParts[packageParts.length - 1];
        }

        String description = clazz.getAnnotation(io.candydoc.ddd.annotations.SharedKernel.class).description();

        return SharedKernel.builder()
            .canonicalName(toCanonicalName(clazz))
            .simpleName(SimpleName.of(simpleName))
            .packageName(toPackageName(clazz))
            .description(Description.of(description))
            .dependencies(dependenciesOf(clazz))
            .build();
    }

    private static ValueObject toValueObject(Class<?> clazz) {
        String description = clazz.getAnnotation(io.candydoc.ddd.annotations.ValueObject.class).description();

        return ValueObject.builder()
            .canonicalName(toCanonicalName(clazz))
            .simpleName(SimpleName.of(clazz.getSimpleName()))
            .packageName(toPackageName(clazz))
            .description(Description.of(description))
            .dependencies(dependenciesOf(clazz))
            .build();
    }

    private static CanonicalName toCanonicalName(Class<?> clazz) {
        return CanonicalName.of(clazz.getCanonicalName());
    }

    private static PackageName toPackageName(Class<?> clazz) {
        return PackageName.of(clazz.getPackageName());
    }

    private static Set<Dependency> dependenciesOf(Class<?> domainModel) {
        Set<Class<?>> dependencies = new HashSet<>();

        Arrays.stream(domainModel.getDeclaredFields())
            .map(Field::getType)
            .forEach(dependencies::add);

        Arrays.stream(domainModel.getDeclaredMethods())
            .forEach(method -> {
                dependencies.add(method.getReturnType());
                dependencies.addAll(Set.of(method.getParameterTypes()));
            });

        return dependencies.stream()
            .flatMap(dependency -> ReflectionsDomainModelFinder.dddPatternOf(dependency)
                .map(dddPattern -> Dependency.builder()
                    .pattern(dddPattern)
                    .name(CanonicalName.of(dependency.getCanonicalName()))
                    .build()))
            .collect(Collectors.toUnmodifiableSet());
    }

    private static Stream<DDDPattern> dddPatternOf(Class<?> clazz) {
        Set<Annotation> annotations = Set.of(clazz.getAnnotations());

        return annotations.stream()
            .filter(annotation -> DDDKeywords.KEYWORDS.contains(annotation.annotationType()))
            .findFirst()
            .map(DDDKeywords::patternOf)
            .stream();
    }
}
