package io.candydoc.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BoundedContextTest {

    @Test
    void throws_an_exception_when_name_is_null() {
        Assertions.assertThatThrownBy(() -> BoundedContext.builder()
                .description("description")
                .build()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void throws_an_exception_when_description_is_null() {
        Assertions.assertThatThrownBy(() -> BoundedContext.builder()
                .name("name")
                .build()).isInstanceOf(NullPointerException.class);
    }
}