package io.candydoc.domain.model;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class ValueObject {
    @NonNull
    String description;
    @NonNull
    String className;
}
