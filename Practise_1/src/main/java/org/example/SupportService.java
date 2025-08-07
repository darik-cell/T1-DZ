package org.example;

import org.example.model.SupportPhrase;

public interface SupportService {
  SupportPhrase getPhrase();

  void setPhrase(SupportPhrase phrase);
}
