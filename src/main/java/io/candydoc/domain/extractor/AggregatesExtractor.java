package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractAggregates;
import io.candydoc.domain.events.AggregateFound;
import io.candydoc.domain.events.DomainEvent;
import org.reflections8.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AggregatesExtractor implements Extractor<ExtractAggregates> {

    @Override
    public List<DomainEvent> extract(ExtractAggregates command) {
        Reflections reflections = new Reflections(command.getPackageToScan());
        Set<Class<?>> aggregatesClass = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.Aggregate.class);
        return aggregatesClass.stream()
            .map(aggregate -> AggregateFound.builder()
                .name(aggregate.getAnnotation(io.candydoc.domain.annotations.Aggregate.class).name())
                .description(aggregate.getAnnotation(io.candydoc.domain.annotations.Aggregate.class).description())
                .className(aggregate.getSimpleName())
                .fullName(aggregate.getName())
                .packageName(aggregate.getPackageName())
                .boundedContext(command.getPackageToScan())
                .build())
            .collect(Collectors.toUnmodifiableList());
    }
}
