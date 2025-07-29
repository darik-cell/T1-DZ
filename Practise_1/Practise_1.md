Для чего в этом блоке передавать .getInterfaces()?
```java
Proxy.newProxyInstance(this.getClass().getClassLoader(),
            object.getClass().getInterfaces(),
            new LoggingInvocationHandler(object))
```

### build.gradle
```groovy
plugins {
    id 'java'
}

group = 'org.example'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.reflections:reflections:0.10.2'
    testImplementation platform('org.junit:junit-bom:5.10.0')
    testImplementation 'org.junit.jupiter:junit-jupiter'
}

test {
    useJUnitPlatform()
}
```

### gradle\wrapper\gradle-wrapper.properties
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.13-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

### settings.gradle
```groovy
rootProject.name = 'Practise_1'
```

### src\main\java\org\example\ApplicationContext.java
```java
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
        List<Object> args = Arrays.stream(method.getParameterTypes())
                .map(instances::get)
                .toList();
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
```

### src\main\java\org\example\configuration\Configuration.java
```java
package org.example.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
}
```

### src\main\java\org\example\configuration\Instance.java
```java
package org.example.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Instance {
}
```

### src\main\java\org\example\configuration\SupportConfiguration.java
```java
package org.example.configuration;

import org.example.SupportManager;
import org.example.SupportManagerImpl;
import org.example.SupportService;
import org.example.SupportServiceImpl;

@Configuration
public class SupportConfiguration {

  @Instance
  public SupportManager supportManager() {
    return new SupportManagerImpl(supportService());
  }

  @Instance
  public SupportService supportService() {
    return new SupportServiceImpl();
  }
}
```

### src\main\java\org\example\Logged.java
```java
package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logged {
}
```

### src\main\java\org\example\LoggingInvocationHandler.java
```java
package org.example;

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
```

### src\main\java\org\example\LoggingSupportManager.java
```java
package org.example;

@Logged
public class LoggingSupportManager implements SupportManager {

  private final SupportManager supportManager;

  public LoggingSupportManager(SupportManager supportManager) {
    this.supportManager = supportManager;
  }

  @Override
  public String provideSupport() {
    System.out.println("Начало метода");
    String supportPhrase = supportManager.provideSupport();
    System.out.println("Конец метода");
    return supportPhrase;
  }
}
```

### src\main\java\org\example\SupportManager.java
```java
package org.example;

public interface SupportManager {

  String provideSupport();
}
```

### src\main\java\org\example\SupportManagerImpl.java
```java
package org.example;

public class SupportManagerImpl implements SupportManager {

  private final SupportService supportService;

  public SupportManagerImpl(SupportService supportService) {
    this.supportService = supportService;
  }

  @Override
  public String provideSupport() {
    return "Dear, %s".formatted(supportService.getPhrase());
  }
}
```

### src\main\java\org\example\SupportService.java
```java
package org.example;

public interface SupportService {

  @Logged
  String getPhrase();
}
```

### src\main\java\org\example\SupportServiceFactory.java
```java
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
```

### src\main\java\org\example\SupportServiceImpl.java
```java
package org.example;

public class SupportServiceImpl implements SupportService {

  @Override
  public String getPhrase() {
    return "Hey!";
  }

}
```

### src\test\java\org\example\ApplicationContextTest.java
```java
package org.example;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApplicationContextTest {

  @Test
  public void application_context_should_return_instance_by_class() throws InvocationTargetException, IllegalAccessException {
    final var applicatoinContext = new ApplicationContext();
    assertEquals(SupportManager.class, applicatoinContext.getInstance(SupportManager.class).getClass());
  }
}
```

### src\test\java\org\example\SupportManagerTest.java
```java
package org.example;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupportManagerTest {

  @Test
  public void support_manager_should_return_support_phrase() throws InvocationTargetException, IllegalAccessException {
    final var context = new ApplicationContext();
    final var supportManager = context.getInstance(SupportManager.class);
    assertEquals("Dear, Hey!", supportManager.provideSupport());
  }
}
```

