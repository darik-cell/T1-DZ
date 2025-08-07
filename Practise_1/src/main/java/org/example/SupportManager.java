package org.example;

import org.example.model.SupportPhrase;

public interface SupportManager {

  SupportPhrase provideSupport();

  void writeSupport(SupportPhrase supportPhrase);
}
