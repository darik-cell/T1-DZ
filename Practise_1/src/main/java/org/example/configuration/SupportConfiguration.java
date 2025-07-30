package org.example.configuration;

import org.example.SupportManager;
import org.example.SupportManagerImpl;
import org.example.SupportService;
import org.example.SupportServiceImpl;
import org.example.proxy.ControllerLoggingProxyApplier;
import org.example.proxy.ProxyApplier;

@Configuration
public class SupportConfiguration {

  @Instance(priority = Integer.MAX_VALUE)
  public ProxyApplier controllerLoggingProxy1() {
    return new ControllerLoggingProxyApplier();
  }

  @Instance(priority = Integer.MAX_VALUE)
  public ProxyApplier controllerLoggingProxy2() {
    return new ControllerLoggingProxyApplier();
  }

  @Instance
  public SupportManager supportManager(SupportService supportService) {
    return new SupportManagerImpl(supportService);
  }

  @Instance
  public SupportService supportService() {
    return new SupportServiceImpl();
  }
}
