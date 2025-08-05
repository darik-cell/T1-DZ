package org.example;

import org.example.model.SupportPhrase;

public class LoggingSupportManager implements SupportManager {

  private final SupportManager supportManager;

  public LoggingSupportManager(SupportManager supportManager) {
    this.supportManager = supportManager;
  }

  @Override
  public SupportPhrase provideSupport() {
    System.out.println("Начало метода");
    var supportPhrase = supportManager.provideSupport();
    System.out.println("Конец метода");
    return supportPhrase;
  }
}
