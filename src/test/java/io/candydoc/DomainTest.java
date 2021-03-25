package io.candydoc;

import io.candydoc.model.BoundedContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class DomainTest {

    private Domain domain;


    @BeforeEach
    public void setUp() {
        domain = new Domain();
    }

    @Test
    void retrieve_bounded_context_one_object_from_project() {
        List<BoundedContext> actual = domain.getBoundedContexts("candydoc.sample");
        Assertions.assertThat(actual)
                .containsExactlyInAnyOrder(
                        BoundedContext.builder()
                                .name("candydoc.sample.bounded_context_one")
                                .description("test package 1")
                                .build(),
                        BoundedContext.builder()
                                .name("candydoc.sample.bounded_context_two")
                                .description("test package 2")
                                .build());
    }

}