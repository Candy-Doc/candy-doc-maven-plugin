package io.candydoc.domain;

public interface SaveDocumentationAdapterFactory {
    SaveDocumentationPort getAdapter(String outputFormat, String outputDirectory);
}

