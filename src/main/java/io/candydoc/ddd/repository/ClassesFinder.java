package io.candydoc.ddd.repository;

import io.candydoc.ddd.model.CanonicalName;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;

public class ClassesFinder {
  private static final ClassesFinder INSTANCE = new ClassesFinder();
  private Set<Element> elements = new HashSet<>();

  public ClassesFinder() {}

  public Element forName(CanonicalName canonicalName) {
    return elements.stream()
        .filter(element -> element.asType().toString().equals(canonicalName.value()))
        .findFirst()
        .orElseThrow();
  }

  public Set<Element> getElements() {
    return elements;
  }

  public Set<Element> getElementsAnnotatedBy(Class<? extends Annotation> annotation) {
    return elements.stream()
        .filter(typeElement -> typeElement.getAnnotation(annotation) != null)
        .collect(Collectors.toSet());
  }

  public boolean addElements(Set<Element> elements) {
    return this.elements.addAll(elements);
  }

  public static ClassesFinder getInstance() {
    return INSTANCE;
  }

  @Override
  public String toString() {
    return super.toString() + elements.toString();
  }
}
