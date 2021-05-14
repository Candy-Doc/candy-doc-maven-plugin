package io.candydoc.domain.command;


import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.lang.annotation.Annotation;

@Builder
@Value
@ToString
public class CheckConceptInteraction implements Command{
    String className;

    public void accept(Visitor visitor) {
        visitor.handle(this);
    }
}