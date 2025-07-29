package org.example.configuration;

import org.example.SupportManager;
import org.example.SupportManagerImpl;
import org.example.SupportService;
import org.example.SupportServiceImpl;

@Configuration
public class SupportConfiguration {

  @Instance
  public SupportManager supportManager(SupportService supportService) {
    return new SupportManagerImpl(supportService);
  }

  @Instance
  public SupportService supportService() {
    return new SupportServiceImpl();
  }
}
