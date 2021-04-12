package io.candydoc.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CoreConceptTest {
    @Test
    void throws_an_exception_when_name_is_null() {
        Assertions.assertThatThrownBy(() -> CoreConcept.builder()
                .description("description")
                .build()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void throws_an_exception_when_description_is_null() {
        Assertions.assertThatThrownBy(() -> CoreConcept.builder()
                .name("name")
                .build()).isInstanceOf(NullPointerException.class);
    }

}