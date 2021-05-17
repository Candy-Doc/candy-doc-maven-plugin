package io.candydoc.domain.extractor;

import io.candydoc.domain.command.Command;
import io.candydoc.domain.events.DomainEvent;

import java.util.List;

public interface Extractor<T extends Command> {
    List<DomainEvent> extract(T command);
}
