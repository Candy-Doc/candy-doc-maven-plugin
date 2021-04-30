package io.candydoc.infra.model;

import lombok.*;

import java.util.List;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@EqualsAndHashCode
public class ValueObjectDto {
    @NonNull
    String description;
    @NonNull
    String className;
    List<String> errors;

    public void addError(String error) {errors.add(error);}
}
