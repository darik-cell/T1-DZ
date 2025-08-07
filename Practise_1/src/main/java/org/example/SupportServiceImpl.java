package org.example;

import org.example.model.SupportPhrase;

public class SupportServiceImpl implements SupportService {

  private SupportPhrase supportPhrase = new SupportPhrase("Hey!");

  @Override
  public SupportPhrase getPhrase() {
    return supportPhrase;
  }

  @Override
  public void setPhrase(SupportPhrase newPhrase) {
    supportPhrase = newPhrase;
  }

}
