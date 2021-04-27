package io.candydoc.infra.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CoreConceptDtoTest {
    @Test
    void throws_an_exception_when_name_is_null() {
        Assertions.assertThatThrownBy(() -> CoreConceptDto.builder()
                .description("description")
                .build()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void throws_an_exception_when_description_is_null() {
        Assertions.assertThatThrownBy(() -> CoreConceptDto.builder()
                .name("name")
                .build()).isInstanceOf(NullPointerException.class);
    }

}