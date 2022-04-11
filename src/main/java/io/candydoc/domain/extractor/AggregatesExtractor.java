package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractAggregates;
import io.candydoc.domain.events.AggregateFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.repository.ClassesFinder;
import io.candydoc.domain.repository.ProcessorUtils;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;

public class AggregatesExtractor implements Extractor<ExtractAggregates> {

  @Override
  public List<DomainEvent> extract(ExtractAggregates command) {
    Set<Element> aggregatesClasses =
        ClassesFinder.getInstance()
            .getClassesAnnotatedBy(io.candydoc.domain.annotations.Aggregate.class);
    return aggregatesClasses.stream()
        .map(
            aggregate ->
                AggregateFound.builder()
                    .name(
                        aggregate
                            .getAnnotation(io.candydoc.domain.annotations.Aggregate.class)
                            .name())
                    .description(
                        aggregate
                            .getAnnotation(io.candydoc.domain.annotations.Aggregate.class)
                            .description())
                    .className(aggregate.asType().toString())
                    .packageName(
                        ProcessorUtils.getInstance()
                            .getElementUtils()
                            .getPackageOf(aggregate)
                            .getSimpleName()
                            .toString())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
