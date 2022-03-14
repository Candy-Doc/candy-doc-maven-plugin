package io.candydoc.infra.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ValueObjectDtoTest {
  @Test
  void throws_an_exception_when_description_is_null() {
    Assertions.assertThatThrownBy(
            () ->
                ConceptDto.builder()
                    .name("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                    .build())
        .isInstanceOf(NullPointerException.class);
  }
}
