package io.candydoc.infra.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DomainEventDtoTest {
    @Test
    void throws_an_exception_when_description_is_null() {
        Assertions.assertThatThrownBy(() -> DomainEventDto.builder()
                .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
                .build()).isInstanceOf(NullPointerException.class);
    }
}