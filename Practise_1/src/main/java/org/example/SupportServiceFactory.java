package org.example;

public class SupportServiceFactory {

  private static final SupportService INSTANCE = init();

  public static SupportService getInstance() {
    return INSTANCE;
  }

  private static SupportService init() {
    return new SupportServiceImpl();
  }
}
