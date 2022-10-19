package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DomainModel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class TacticalPattern extends DomainModel {
}
