package io.candydoc.domain.command;


import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

import java.lang.annotation.Annotation;

@Builder
@Value
@ToString
public class CheckConceptInteractions implements Command {
    @NonNull
    String className;

    public void accept(Visitor visitor) {
        visitor.handle(this);
    }
}
