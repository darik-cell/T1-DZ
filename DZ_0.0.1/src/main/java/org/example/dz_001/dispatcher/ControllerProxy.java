package org.example.dz_001.dispatcher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ControllerProxy implements InvocationHandler {

  private final Object target;

  public ControllerProxy(Object target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println("Вызов метода: " + method.getName() + " с параметрами: " + Arrays.toString(args));
    return method.invoke(target, args);
  }

  public static Object wrap(Object bean) {
    return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new ControllerProxy(bean));
  }
}
