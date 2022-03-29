package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainCommands;
import io.candydoc.domain.events.DomainCommandFound;
import io.candydoc.domain.events.DomainEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.candydoc.domain.repository.ClassesFinder;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import javax.lang.model.element.TypeElement;

@Slf4j
public class DomainCommandExtractor implements Extractor<ExtractDomainCommands> {

  @Override
  public List<DomainEvent> extract(ExtractDomainCommands command) {
    Set<TypeElement> domainCommandClasses = ClassesFinder.getInstance().getClassesAnnotatedBy(io.candydoc.domain.annotations.DomainCommand.class);
    log.info("Domain commands found in {}: {}", command.getPackageToScan(), domainCommandClasses);
    return domainCommandClasses.stream()
        .map(
            domainCommand ->
                DomainCommandFound.builder()
                    .description(
                        domainCommand
                            .getAnnotation(io.candydoc.domain.annotations.DomainCommand.class)
                            .description())
                    .name(domainCommand.getSimpleName().toString())
                    .className(domainCommand.getSimpleName().toString())
                    .packageName(domainCommand.getClass().getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
