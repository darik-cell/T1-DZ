package org.example;

import org.example.model.SupportPhrase;

public class SupportServiceImpl implements SupportService {

  @Override
  public SupportPhrase getPhrase() {
    return new SupportPhrase("Hey!");
  }

}
