package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainCommands;
import io.candydoc.domain.events.DomainCommandFound;
import io.candydoc.domain.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DomainCommandExtractor implements Extractor<ExtractDomainCommands> {

    @Override
    public List<DomainEvent> extract(ExtractDomainCommands command) {
        Reflections reflections = new Reflections(command.getPackageToScan());
        Set<Class<?>> domainCommandClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.DomainCommand.class);
        log.info("Domain commands found in {}: {}", command.getPackageToScan(), domainCommandClasses);
        return domainCommandClasses.stream()
            .map(domainCommand -> DomainCommandFound.builder()
                .description(domainCommand.getAnnotation(io.candydoc.domain.annotations.DomainCommand.class).description())
                .name(domainCommand.getSimpleName())
                .className(domainCommand.getName())
                .packageName(domainCommand.getPackageName())
                .boundedContext(command.getPackageToScan())
                .build())
            .collect(Collectors.toUnmodifiableList());
    }
}
