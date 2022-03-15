package io.candydoc.domain;

import io.candydoc.domain.events.DomainEvent;
import java.io.IOException;
import java.util.List;

public interface SaveDocumentationPort {
  void save(List<DomainEvent> domainEvents) throws IOException;
}
