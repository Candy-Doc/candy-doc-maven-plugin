package io.candydoc.ddd.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FullNameTest {

    @Test
    void value_is_mandatory() {
        assertThatThrownBy(() -> FullName.of(null))
                .isInstanceOf(NullPointerException.class);
    }
}