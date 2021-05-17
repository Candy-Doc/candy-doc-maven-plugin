package io.candydoc.infra.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@ToString
@Value
@EqualsAndHashCode(callSuper = true)
public class ValueObjectDto extends ConceptDto {

    List<String> errors;

    public void addError(String error) {
        errors.add(error);
    }
}