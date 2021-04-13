package io.candydoc.domain.model;

import lombok.*;

import java.util.List;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class BoundedContext {
    @NonNull
    String name;
    @NonNull
    String description;
    @Singular
    List<CoreConcept> coreConcepts;
    @Singular
    List<ValueObject> valueObjects;
}
