package io.candydoc.ddd.extract_domain_models;

import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;

import java.util.Set;

public interface DomainModelFinder {

  Set<DomainModel> findDomainModels(PackageName packageToScan);

}
