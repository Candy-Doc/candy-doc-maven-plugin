package io.candydoc.plugin.save_documentation;

import io.candydoc.ddd.extract_ddd_concepts.SaveDocumentationPort;

public interface SaveDocumentationAdapterFactory {
  SaveDocumentationPort getAdapter(String outputFormat, String outputDirectory);
}
