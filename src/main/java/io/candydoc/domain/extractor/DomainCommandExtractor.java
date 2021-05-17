package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainCommand;
import io.candydoc.domain.events.DomainCommandFound;
import io.candydoc.domain.events.DomainEvent;
import org.reflections8.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DomainCommandExtractor implements Extractor<ExtractDomainCommand> {

    @Override
    public List<DomainEvent> extract(ExtractDomainCommand command) {
        Reflections reflections = new Reflections(command.getPackageToScan());
        Set<Class<?>> domainCommands = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.DomainCommand.class);
        return domainCommands.stream()
                .map(domainCommand -> DomainCommandFound.builder()
                        .description(domainCommand.getAnnotation(io.candydoc.domain.annotations.DomainCommand.class).description())
                        .className(domainCommand.getSimpleName())
                        .fullName(domainCommand.getName())
                        .packageName(domainCommand.getPackageName())
                        .boundedContext(command.getPackageToScan())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }
}
