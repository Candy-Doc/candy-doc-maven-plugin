package io.candydoc.ddd.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class FullNameTest {

  @Test
  void value_is_mandatory() {
    assertThatThrownBy(() -> FullName.of(null)).isInstanceOf(NullPointerException.class);
  }
}
