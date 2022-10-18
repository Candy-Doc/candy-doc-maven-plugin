package io.candydoc.plugin.save_documentation;

import io.candydoc.ddd.extract_domain_models.SaveUbiquitousLanguagePort;

public interface SaveDocumentationAdapterFactory {
  SaveUbiquitousLanguagePort getAdapter(String outputFormat, String outputDirectory);
}
