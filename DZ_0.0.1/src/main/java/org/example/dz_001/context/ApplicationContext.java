package org.example.dz_001.context;

import org.example.dz_001.configuration.Configuration;
import org.example.dz_001.configuration.Instance;
import org.example.dz_001.proxy.ProxyApplier;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ApplicationContext {
  private final Map<String, Object> instances = new HashMap<>();
  private final List<Method> instanceMethods = new ArrayList<>();
  private final Map<Class<?>, Object> configInstances = new HashMap<>();
  private final Set<String> instancesInProgress = new HashSet<>();

  public ApplicationContext(String packageName) {
    scanConfigurationClasses(packageName);
    init();
  }

  private void scanConfigurationClasses(String packageName) {
    Reflections reflections = new Reflections(packageName);
    Set<Class<?>> configurationClasses = reflections.getTypesAnnotatedWith(Configuration.class);
    for (Class<?> configClass : configurationClasses) {
      try {
        Object configInstance = configClass.getDeclaredConstructor().newInstance();
        configInstances.put(configClass, configInstance);
        for (Method method : configClass.getDeclaredMethods()) {
          if (method.isAnnotationPresent(Instance.class)) {
            instanceMethods.add(method);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public <T> T getInstance(Class<T> instanceType) {
    return instanceType.cast(instances.values().stream()
            .filter(instanceType::isInstance)
            .findFirst()
            .orElseGet(() -> createInstanceByType(instanceType)));
  }

  public <T> List<T> getInstances(Class<T> instanceType) {
    return (List<T>) instances.values().stream()
            .filter(instanceType::isInstance)
            .toList();
  }

  private void init() {
    instanceMethods.stream()
            .sorted(Comparator.comparingInt((Method m) -> m.getAnnotation(Instance.class).priority()).reversed())
            .forEach(m -> createInstance(m.getName(), m));
  }

  private Object applyProxies(Object o) {
    var result = o;
    for (ProxyApplier applier : getInstances(ProxyApplier.class)) {
      result = applier.apply(result);
    }
    return result;
  }

  private Object createInstanceByType(Class<?> instanceType) {
    for (Method method : instanceMethods) {
      if (instanceType.isAssignableFrom(method.getReturnType())) {
        return createInstance(method.getName(), method);
      }
    }
    throw new RuntimeException("No instance found of type " + instanceType.getName());
  }

  private Object createInstance(String instanceName, Method method) {
    if (instancesInProgress.contains(instanceName)) {
      throw new RuntimeException("Circular dependency detected for instance: " + instanceName);
    }
    instancesInProgress.add(instanceName);

    try {
      Object configInstance = configInstances.get(method.getDeclaringClass());
      Object[] dependencies = resolveDependencies(method);
      Object instance = method.invoke(configInstance, dependencies);
      instances.put(instanceName, applyProxies(instance));
      return instance;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create instance: " + instanceName, e);
    } finally {
      instancesInProgress.remove(instanceName);
    }
  }

  private Object[] resolveDependencies(Method method) {
    return Arrays.stream(method.getParameterTypes())
            .map(this::getInstance)
            .toArray();
  }

  public List<?> getAllInstances() {
    return new ArrayList<>(instances.values());
  }
}
