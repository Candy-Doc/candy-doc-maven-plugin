package io.candydoc.domain.model;

import lombok.*;

import java.util.Set;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class CoreConcept {
    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    String className;
    @Singular("interactsWith")
    Set<String> interactsWith;
}
