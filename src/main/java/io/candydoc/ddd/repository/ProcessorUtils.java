package io.candydoc.ddd.repository;

import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class ProcessorUtils {
  private static final ProcessorUtils INSTANCE = new ProcessorUtils();
  private Messager messager;
  private Elements elementsUtils;
  private Types typesUtils;

  public ProcessorUtils() {}

  public Elements getElementUtils() {
    return elementsUtils;
  }

  public void setElementsUtils(Elements elementsUtils) {
    this.elementsUtils = elementsUtils;
  }

  public Types getTypesUtils() {
    return typesUtils;
  }

  public void setTypesUtils(Types typesUtils) {
    this.typesUtils = typesUtils;
  }

  public Messager getMessager() {
    return messager;
  }

  public void setMessager(Messager messager) {
    this.messager = messager;
  }

  public static ProcessorUtils getInstance() {
    return INSTANCE;
  }
}
