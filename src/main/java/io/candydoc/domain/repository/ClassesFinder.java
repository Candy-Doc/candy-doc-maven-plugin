package io.candydoc.domain.repository;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassesFinder {
    private final static ClassesFinder INSTANCE = new ClassesFinder();
    private Set<TypeElement> typeElements = new HashSet<>();

    public ClassesFinder() {
    }

    public Set<TypeElement> getClasses() {
        return typeElements;
    }

    public Set<TypeElement> getClassesAnnotatedBy(Class<? extends Annotation> annotation) {
        return typeElements
                .stream()
                .filter(typeElement -> typeElement.getAnnotation(annotation) != null)
                .collect(Collectors.toSet());
    }

    public boolean addElements(Set<TypeElement> elements) {
        return typeElements.addAll(elements);
    }

    public static ClassesFinder getInstance() {
        return INSTANCE;
    }
}
