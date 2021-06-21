package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractValueObjects;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.ValueObjectFound;
import org.reflections8.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValueObjectExtractor implements Extractor<ExtractValueObjects> {

    public List<DomainEvent> extract(ExtractValueObjects command) {
        Reflections reflections = new Reflections(command.getPackageToScan());
        Set<Class<?>> valueObjectClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.ValueObject.class);
        return valueObjectClasses.stream()
                .map(valueObject -> ValueObjectFound.builder()
                        .description(valueObject.getAnnotation(io.candydoc.domain.annotations.ValueObject.class).description())
                        .className(valueObject.getSimpleName())
                        .fullName(valueObject.getName())
                        .packageName(valueObject.getPackageName())
                        .boundedContext(command.getPackageToScan())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }
}
