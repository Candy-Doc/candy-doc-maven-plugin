package io.candydoc.ddd.extract_domain_models;

public class PackageToScanMissing extends ExtractionException {
    public PackageToScanMissing(String errorMessage) {
        super(errorMessage);
    }
}
