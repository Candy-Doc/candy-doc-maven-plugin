package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractValueObjects;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.ValueObjectFound;
import lombok.extern.slf4j.Slf4j;
import org.netbeans.lib.cvsclient.commandLine.command.log;
import org.reflections8.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ValueObjectExtractor implements Extractor<ExtractValueObjects> {

    public List<DomainEvent> extract(ExtractValueObjects command) {
        Reflections reflections = new Reflections(command.getPackageToScan());
        Set<Class<?>> valueObjectClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.ValueObject.class);
        log.info("Value objects found in {}: {}", command.getPackageToScan(), valueObjectClasses);
        return valueObjectClasses.stream()
            .map(valueObject -> ValueObjectFound.builder()
                .description(valueObject.getAnnotation(io.candydoc.domain.annotations.ValueObject.class).description())
                .name(valueObject.getSimpleName())
                .className(valueObject.getName())
                .packageName(valueObject.getPackageName())
                .boundedContext(command.getPackageToScan())
                .build())
            .collect(Collectors.toUnmodifiableList());
    }

}
