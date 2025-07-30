package org.example.dz_001.proxy;

import org.example.dz_001.annotation.Logged;
import org.example.dz_001.controller.annotation.Controller;

import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ControllerLoggingProxyApplier implements ProxyApplier {
  @Override
  public Object apply(Object o) {
    final var shouldProxyBeApplied = Arrays.stream(o.getClass().getInterfaces())
            .anyMatch(it -> it.isAnnotationPresent(Controller.class));
    if (shouldProxyBeApplied) {
      return Proxy.newProxyInstance(
              this.getClass().getClassLoader(),
              o.getClass().getInterfaces(),
              (proxy, method, args) -> {
                if (method.isAnnotationPresent(Logged.class)) {
                  System.out.println("Старт метода");
                  final var result = method.invoke(o, args);
                  System.out.println("Конец метода!");
                  return result;
                }
                else {
                  return method.invoke(o, args);
                }
              }
      )
    }

    return null;
  }
}
