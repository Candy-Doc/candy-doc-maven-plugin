package io.candydoc.ddd.extract_domain_vocabulary;

public class UbiquitousLanguageSavingFailed extends RuntimeException {
  public UbiquitousLanguageSavingFailed(Throwable throwable) {
    super(throwable);
  }
}
