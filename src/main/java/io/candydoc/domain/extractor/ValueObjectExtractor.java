package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractCoreConcept;
import io.candydoc.domain.command.ExtractValueObject;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.InteractionBetweenConceptFound;
import io.candydoc.domain.events.ValueObjectFound;
import io.candydoc.domain.events.WrongUsageOfValueObjectFound;
import org.reflections8.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ValueObjectExtractor implements Extractor<ExtractValueObject> {

    public List<DomainEvent> extract(ExtractValueObject command) {
        Reflections reflections = new Reflections(command.getPackageToScan());
        Set<Class<?>> valueObjectClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.ValueObject.class);
        List<DomainEvent> valueObjects = valueObjectClasses.stream()
                .map(valueObject -> ValueObjectFound.builder()
                        .description(valueObject.getAnnotation(io.candydoc.domain.annotations.ValueObject.class).description())
                        .className(valueObject.getName())
                        .boundedContext(command.getPackageToScan())
                        .build())
                .collect(Collectors.toUnmodifiableList());
        return valueObjects;
    }
}
