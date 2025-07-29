package org.example;

import org.example.configuration.Configuration;
import org.example.configuration.Instance;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class ApplicationContext {

  private final Map<Class<?>, Object> instances = new HashMap<>();

  public ApplicationContext() throws InvocationTargetException, IllegalAccessException {
    Reflections reflections = new Reflections("org.example.configuration");
    final var configurations = reflections.getTypesAnnotatedWith(Configuration.class)
            .stream()
            .map(type -> {
              try {
                return type.getDeclaredConstructor().newInstance();
              } catch (InstantiationException e) {
                throw new RuntimeException(e);
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
              } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
              }
            })
            .toList();
    for (Object configuration : configurations) {
      List<Method> methods = Arrays.stream(configuration.getClass().getMethods())
              .filter(method -> method.isAnnotationPresent(Instance.class))
              .toList();
      List<Method> methodsWithoutParameters = methods.stream()
              .filter(method -> method.getParameterCount() == 0)
              .toList();
      List<Method> methodsWithParameters = methods.stream()
              .filter(method -> method.getParameterCount() > 0)
              .toList();
      for (var method : methodsWithoutParameters) {
        instances.put(method.getReturnType(), wrapWithLoggingProxy(method.invoke(configuration)));
      }
      for (var method : methodsWithParameters) {
        Object[] args = Arrays.stream(method.getParameterTypes())
                .map(instances::get)
                .toArray();
        instances.put(method.getReturnType(), wrapWithLoggingProxy(method.invoke(configuration, args)));
      }
    }
  }

  private Object wrapWithLoggingProxy(Object object) {
    return Proxy.newProxyInstance(this.getClass().getClassLoader(),
            object.getClass().getInterfaces(),
            new LoggingInvocationHandler(object)
    );
  }

  public <T> T getInstance(Class<T> clazz) {
    return (T) Optional.ofNullable(instances.get(clazz))
            .orElseThrow();
  }


}
