package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainCommands;
import io.candydoc.domain.events.DomainCommandFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.repository.ClassesFinder;
import io.candydoc.domain.repository.ProcessorUtils;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;

public class DomainCommandExtractor implements Extractor<ExtractDomainCommands> {

  @Override
  public List<DomainEvent> extract(ExtractDomainCommands command) {
    Set<Element> domainCommandClasses =
        ClassesFinder.getInstance()
            .getClassesAnnotatedBy(io.candydoc.domain.annotations.DomainCommand.class);
    return domainCommandClasses.stream()
        .map(
            domainCommand ->
                DomainCommandFound.builder()
                    .description(
                        domainCommand
                            .getAnnotation(io.candydoc.domain.annotations.DomainCommand.class)
                            .description())
                    .name(domainCommand.getSimpleName().toString())
                    .className(domainCommand.asType().toString())
                    .packageName(
                        ProcessorUtils.getInstance()
                            .getElementUtils()
                            .getPackageOf(domainCommand)
                            .getSimpleName()
                            .toString())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
