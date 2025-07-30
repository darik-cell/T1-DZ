package org.example;

import org.example.proxy.annotation.Logged;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LoggingInvocationHandler implements InvocationHandler {

  private final Object target;

  public LoggingInvocationHandler(Object target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.isAnnotationPresent(Logged.class)) {
      System.out.println("Старт метода!");
      final var result = method.invoke(target, args);
      System.out.println("Конец метода!");
      return result;
    }

    return method.invoke(target, args);
  }
}
