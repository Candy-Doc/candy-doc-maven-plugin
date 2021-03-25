package io.candydoc.model;

import lombok.*;

import java.util.Objects;

@Builder
@ToString
@Value
public class BoundedContext {

    String name;
    String description;
}
