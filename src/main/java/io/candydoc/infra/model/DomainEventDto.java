package io.candydoc.infra.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Value
@EqualsAndHashCode(callSuper = true)
public class DomainEventDto extends ConceptDto {
}
