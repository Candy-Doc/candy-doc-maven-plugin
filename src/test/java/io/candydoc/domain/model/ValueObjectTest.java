package io.candydoc.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ValueObjectTest {

    @Test
    void throws_an_exception_when_description_is_null() {
        Assertions.assertThatThrownBy(() -> ValueObject.builder()
                .build()).isInstanceOf(NullPointerException.class);
    }
}