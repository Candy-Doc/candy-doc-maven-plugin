package io.candydoc.domain.exceptions;

import javax.lang.model.element.Element;

public class ElementNotAnnotatedByDDDConcept extends DomainException {
  public ElementNotAnnotatedByDDDConcept(Element wrongClass) {
    super("Element: " + wrongClass.getSimpleName().toString() + " is not annotated by DDD concept");
  }
  ;
}
