package io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z;

import io.candydoc.ddd.annotations.DomainService;

@DomainService
public class DomainService_Z {
  private Repository_Z repository;

  public void publishDomainEvent_Z(DomainEvent_Z eventToPublish) {}
  ;
}
