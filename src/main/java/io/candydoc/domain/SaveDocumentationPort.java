package io.candydoc.domain;
import io.candydoc.domain.model.BoundedContext;

import java.io.IOException;
import java.util.List;

public interface SaveDocumentationPort {
    void save(List<BoundedContext> boundedContexts) throws IOException;
}
