package io.candydoc.domain.repository;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;

public class ClassesFinder {
  private static final ClassesFinder INSTANCE = new ClassesFinder();
  private Set<Element> elements = new HashSet<>();

  public ClassesFinder() {}

  public Set<Element> getClasses() {
    return elements;
  }

  public Set<Element> getClassesAnnotatedBy(Class<? extends Annotation> annotation) {
    return elements.stream()
        .filter(typeElement -> typeElement.getAnnotation(annotation) != null)
        .collect(Collectors.toSet());
  }

  public boolean addElements(Set<Element> elements) {
    return this.elements.addAll(elements);
  }

  public boolean addElement(Element element) {
    return elements.add(element);
  }

  public static ClassesFinder getInstance() {
    return INSTANCE;
  }
}
