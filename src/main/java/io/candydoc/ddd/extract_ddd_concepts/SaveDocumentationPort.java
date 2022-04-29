package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.Event;
import java.io.IOException;
import java.util.List;

public interface SaveDocumentationPort {
  void save(List<Event> domainEvents) throws IOException;
}
