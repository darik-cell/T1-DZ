package org.example.dz_001.context;

import org.example.dz_001.configuration.Configuration;
import org.example.dz_001.configuration.Instance;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ApplicationContext {

  private final Map<Class<?>, Object> instances = new HashMap<>();

  public <T> T getInstance(Class<T> clazz) {
    return (T) Optional.ofNullable(instances.get(clazz))
            .orElseThrow(() -> new IllegalArgumentException("Нет такого компонента: " + clazz));
  }

  public ApplicationContext() {
    Reflections reflections = new Reflections("org.example.dz_001.configuration");
    final var configs = reflections.getTypesAnnotatedWith(Configuration.class)
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
    for (final var config : configs) {
      Arrays.stream(config.getClass().getMethods())
              .filter(method -> method.isAnnotationPresent(Instance.class))
              .forEach(method -> {
                try {
                  instances.put(method.getReturnType(), method.invoke(config));
                } catch (IllegalAccessException e) {
                  throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                  throw new RuntimeException(e);
                }
              });
    }
  }
}
