package org.example.controller;

import org.example.SupportManager;
import org.example.controller.annotation.RequestMapping;
import org.example.model.SupportPhrase;

public class HelpControllerImpl implements HelpController {

  private final SupportManager supportManager;

  public HelpControllerImpl(SupportManager supportManager) {
    this.supportManager = supportManager;
  }

  @Override
  public SupportPhrase getSupportPhrase() {
    return supportManager.provideSupport();
  }
}
