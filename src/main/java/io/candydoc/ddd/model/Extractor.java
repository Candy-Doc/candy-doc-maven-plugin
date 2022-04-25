package io.candydoc.ddd.model;

import io.candydoc.ddd.Command;
import io.candydoc.ddd.Event;
import java.util.List;

public interface Extractor<T extends Command> {
  List<Event> extract(T command);
}
