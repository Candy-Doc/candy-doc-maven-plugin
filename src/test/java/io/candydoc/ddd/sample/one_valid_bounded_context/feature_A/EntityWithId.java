package io.candydoc.ddd.sample.one_valid_bounded_context.feature_A;

import io.candydoc.ddd.annotations.DomainEntity;
import io.candydoc.ddd.annotations.DomainEntityId;

@DomainEntity
public class EntityWithId {
  @DomainEntityId private int id;
}
