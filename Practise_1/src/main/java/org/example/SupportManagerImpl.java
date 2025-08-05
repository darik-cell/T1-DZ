package org.example;

import org.example.model.SupportPhrase;

public class SupportManagerImpl implements SupportManager {

  private final SupportService supportService;

  public SupportManagerImpl(SupportService supportService) {
    this.supportService = supportService;
  }

  @Override
  public SupportPhrase provideSupport() {
    return supportService.getPhrase();
  }
}
