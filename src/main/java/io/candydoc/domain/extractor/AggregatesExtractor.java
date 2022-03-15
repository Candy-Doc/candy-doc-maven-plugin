package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractAggregates;
import io.candydoc.domain.events.AggregateFound;
import io.candydoc.domain.events.DomainEvent;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
public class AggregatesExtractor implements Extractor<ExtractAggregates> {

  @Override
  public List<DomainEvent> extract(ExtractAggregates command) {
    Reflections reflections = new Reflections(command.getPackageToScan());
    Set<Class<?>> aggregatesClasses =
        reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.Aggregate.class);
    log.info("Aggregates found in {}: {}", command.getPackageToScan(), aggregatesClasses);
    return aggregatesClasses.stream()
        .map(
            aggregate ->
                AggregateFound.builder()
                    .name(getSimpleName(aggregate))
                    .description(
                        aggregate
                            .getAnnotation(io.candydoc.domain.annotations.Aggregate.class)
                            .description())
                    .className(aggregate.getName())
                    .packageName(aggregate.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private String getSimpleName(Class<?> aggregate) {
    String annotatedName =
        aggregate.getAnnotation(io.candydoc.domain.annotations.Aggregate.class).name();
    return annotatedName.isBlank() ? aggregate.getSimpleName() : annotatedName;
  }
}
