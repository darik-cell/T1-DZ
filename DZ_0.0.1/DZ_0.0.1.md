В SupportController не надо работать с HttpServletRequest,HttpServletResponse принимай готовый объект модели StringRequest или вроде того, и использовать Jackson для сериализации

Исправь тесты

### build.gradle
```groovy
plugins {
    id 'java'
    id 'war'
}

group 'org.example'
version '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

ext {
    junitVersion = '5.11.0'
    mockitoVersion = '5.12.0'
    h2Version = '2.2.224'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
}

dependencies {
    implementation 'org.reflections:reflections:0.10.2'
    compileOnly('jakarta.servlet:jakarta.servlet-api:6.1.0')

    implementation("com.h2database:h2:${h2Version}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation 'jakarta.servlet:jakarta.servlet-api:6.1.0'
}

test {
    useJUnitPlatform()
}

tasks.named('war') {
    archiveBaseName = 'help-service'
    archiveVersion = ''
}
```

### docker-compose.yml
```yaml
services:
  help-service:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - h2data:/opt/h2-data
    environment:
      - H2_DB_URL=jdbc:h2:/opt/h2-data/support;AUTO_SERVER=TRUE

volumes:
  h2data:
```

### Dockerfile
```dockerfile
# ---------- Stage 1: build ----------
FROM gradle:8.8-jdk17 AS build
WORKDIR /project
COPY . .
RUN gradle clean war --no-daemon

# ---------- Stage 2: runtime ----------
FROM tomcat:10.1-jdk17
# выключаем дефолтные приложения Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*
# копируем наш WAR
COPY --from=build /project/build/libs/help-service.war /usr/local/tomcat/webapps/help-service.war
# папка для файла базы
RUN mkdir -p /opt/h2-data
EXPOSE 8080
```

### DZ_0.0.1.iml
```
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$" />
    <orderEntry type="jdk" jdkName="temurin-17" jdkType="JavaSDK" />
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
```

### gradle\wrapper\gradle-wrapper.properties
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.8-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

### settings.gradle
```groovy
rootProject.name = "DZ_0-0-1"
```

### src\main\java\org\example\dz_001\bootstrap\ContextLoader.java
```java
package org.example.dz_001.bootstrap;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.dz_001.context.ApplicationContext;

@WebListener
public class ContextLoader implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ApplicationContext context = new ApplicationContext();
    sce.getServletContext().setAttribute("context", context);
  }
}
```

### src\main\java\org\example\dz_001\configuration\AppConfig.java
```java
package org.example.dz_001.configuration;

import org.example.dz_001.MessageRepository;
import org.example.dz_001.Repository;
import org.example.dz_001.controller.annotation.SupportController;

@Configuration
public class AppConfig {

  @Instance
  public Repository repository() {
    return new MessageRepository();
  }

  @Instance
  public SupportController supportController(Repository repository) {
    return new SupportController(repository);
  }
}
```

### src\main\java\org\example\dz_001\configuration\Configuration.java
```java
package org.example.dz_001.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {
}
```

### src\main\java\org\example\dz_001\configuration\Instance.java
```java
package org.example.dz_001.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Instance {
}
```

### src\main\java\org\example\dz_001\context\ApplicationContext.java
```java
package org.example.dz_001.context;

import org.example.dz_001.configuration.Configuration;
import org.example.dz_001.configuration.Instance;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ApplicationContext {

  private final Map<Class<?>, Object> instances = new HashMap<>();

  public <T> T getInstance(Class<T> clazz) {
    return (T) Optional.ofNullable(instances.get(clazz))
            .orElseThrow(() -> new IllegalArgumentException("Нет такого компонента: " + clazz));
  }

  public ApplicationContext() throws InvocationTargetException, IllegalAccessException {
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
    for (Object configuration : configs) {
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
        instances.put(method.getReturnType(), method.invoke(configuration));
      }
      for (var method : methodsWithParameters) {
        Object[] args = Arrays.stream(method.getParameterTypes())
                .map(instances::get)
                .toArray();
        instances.put(method.getReturnType(), method.invoke(configuration, args));
      }
    }
  }

  public List<?> getAllInstances() {
    return new ArrayList<>(instances.values());
  }
}
```

### src\main\java\org\example\dz_001\controller\annotation\Controller.java
```java
package org.example.dz_001.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Controller {
}
```

### src\main\java\org\example\dz_001\controller\annotation\GetMapping.java
```java
package org.example.dz_001.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface GetMapping {
  String value() default "";
}
```

### src\main\java\org\example\dz_001\controller\annotation\PostMapping.java
```java
package org.example.dz_001.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface PostMapping {
  String value() default "";
}
```

### src\main\java\org\example\dz_001\controller\annotation\SupportController.java
```java
package org.example.dz_001.controller.annotation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dz_001.Repository;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class SupportController {

  private final Repository repo;

  public SupportController(Repository repository) {
    this.repo = repository;
  }

  @GetMapping("/v1/support")
  public void getAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain; charset=utf-8");
    for (String m : repo.findAll()) resp.getWriter().println(m);
  }

  @PostMapping("/v1/support")
  public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String body = req.getReader().lines().collect(Collectors.joining("\n")).trim();
    if (body.isEmpty()) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Body is empty");
      return;
    }
    repo.save(body);
    resp.setStatus(HttpServletResponse.SC_CREATED);
  }
}
```

### src\main\java\org\example\dz_001\dispatcher\AnnotationHandlerMapping.java
```java
package org.example.dz_001.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dz_001.context.ApplicationContext;
import org.example.dz_001.controller.annotation.Controller;
import org.example.dz_001.controller.annotation.GetMapping;
import org.example.dz_001.controller.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

public class AnnotationHandlerMapping implements HandlerMapping {

  private final Map<String, HandlerMethod> handlerMethods = new HashMap<>();

  public AnnotationHandlerMapping(ApplicationContext ctx) {
    ctx.getAllInstances().stream()
            .filter(i -> i.getClass().isAnnotationPresent(Controller.class))
            .forEach(this::scanController);
  }

  private void scanController(Object bean) {
    for (var m : bean.getClass().getDeclaredMethods()) {
      if (m.isAnnotationPresent(GetMapping.class)) {
        handlerMethods.put("GET" + m.getAnnotation(GetMapping.class).value(),
                new HandlerMethod(m, ControllerProxy.wrap(bean), m.getAnnotation(GetMapping.class).value(), "GET"));
      }
      if (m.isAnnotationPresent(PostMapping.class)) {
        handlerMethods.put("POST" + m.getAnnotation(PostMapping.class).value(),
                new HandlerMethod(m, ControllerProxy.wrap(bean), m.getAnnotation(PostMapping.class).value(), "POST"));
      }
    }
  }

  @Override
  public HandlerMethod lookup(HttpServletRequest req) {
    return handlerMethods.get(req.getMethod() + " " + req.getRequestURI());
  }
}
```

### src\main\java\org\example\dz_001\dispatcher\ControllerProxy.java
```java
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
```

### src\main\java\org\example\dz_001\dispatcher\DispatcherServlet.java
```java
package org.example.dz_001.dispatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dz_001.context.ApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "dispatcher", urlPatterns = "/*")
public class DispatcherServlet {

  private List<HandlerMapping> mappings;
  private List<HandlerAdapter> adapters;

  public void init() {
    var ctx = new ApplicationContext();
    mappings = List.of(new AnnotationHandlerMapping(ctx));
    adapters = List.of(new ReflectionHandlerAdapter());
  }

  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      var hm = findHandler(req);
      var adapter = findAdapter(hm);
      adapter.handle(req, resp, hm);
    }
    catch (Exception e) {}
  }

  private HandlerMethod findHandler(HttpServletRequest req) {
    return mappings.stream()
            .map(m -> m.lookup(req))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No HandlerMapping found"));
  }

  private HandlerAdapter findAdapter(HandlerMethod hm) {
    return adapters.stream()
            .filter(a -> a.supports(hm))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No HandlerAdapter found"));
  }
}
```

### src\main\java\org\example\dz_001\dispatcher\HandlerAdapter.java
```java
package org.example.dz_001.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
  boolean supports(HandlerMethod handlerMethod);
  void handle(HttpServletRequest req, HttpServletResponse resp, HandlerMethod hm) throws Exception;
}
```

### src\main\java\org\example\dz_001\dispatcher\HandlerMapping.java
```java
package org.example.dz_001.dispatcher;

import jakarta.servlet.http.HttpServletRequest;

public interface HandlerMapping {

  HandlerMethod lookup(HttpServletRequest req);
}
```

### src\main\java\org\example\dz_001\dispatcher\HandlerMethod.java
```java
package org.example.dz_001.dispatcher;

import java.lang.reflect.Method;

public record HandlerMethod(
        Method method,
        Object bean,
        String path,
        String http
) {
}
```

### src\main\java\org\example\dz_001\dispatcher\ReflectionHandlerAdapter.java
```java
package org.example.dz_001.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ReflectionHandlerAdapter implements HandlerAdapter {
  @Override
  public boolean supports(HandlerMethod hm) {
    Class<?>[] p = hm.method().getParameterTypes();
    return p.length == 2
            && HttpServletRequest.class.isAssignableFrom(p[0])
            && HttpServletResponse.class.isAssignableFrom(p[1]);
  }

  @Override
  public void handle(HttpServletRequest req, HttpServletResponse resp, HandlerMethod hm) throws Exception {
    hm.method().invoke(hm.bean(), req, resp);
  }
}
```

### src\main\java\org\example\dz_001\MessageRepository.java
```java
package org.example.dz_001;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository implements Repository{
  private final String url;
  private static final String USER = "sa";
  private static final String PASS = "";

  static {
    try {
      Class.forName("org.h2.Driver");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("H2 driver not found on classpath", e);
    }
  }

  public MessageRepository() {
    this.url = System.getenv().getOrDefault("H2_DB_URL", "jdbc:h2:/opt/h2-data/support;AUTO_SERVER=TRUE");
    initSchema();
  }

  public List<String> findAll() {
    try (Connection c = DriverManager.getConnection(url, USER, PASS);
         PreparedStatement ps = c.prepareStatement("SELECT text FROM messages ORDER BY id");
         ResultSet rs = ps.executeQuery()) {

      List<String> list = new ArrayList<>();
      while (rs.next()) list.add(rs.getString(1));
      return list;
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void save(String msg) {
    try (Connection c = DriverManager.getConnection(url, USER, PASS);
         PreparedStatement ps = c.prepareStatement("INSERT INTO messages(text) VALUES (?)")) {
      ps.setString(1, msg);
      ps.executeUpdate();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  MessageRepository(String urlForTest) {
    this.url = urlForTest;
    initSchema();
  }

  private void initSchema() {
    try (Connection c = DriverManager.getConnection(url, USER, PASS);
         Statement st = c.createStatement()) {

      st.executeUpdate("""
              CREATE TABLE IF NOT EXISTS messages(
                  id IDENTITY PRIMARY KEY,
                  text VARCHAR(255) NOT NULL
              )
              """);

      try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM messages")) {
        rs.next();
        if (rs.getInt(1) == 0) {
          st.executeUpdate("INSERT INTO messages(text) VALUES ('Ты справишься!')");
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
}
```

### src\main\java\org\example\dz_001\Repository.java
```java
package org.example.dz_001;

import java.util.List;

public interface Repository {

  List<String> findAll();
  void save(String msg);
}
```

### src\main\webapp\index.jsp
```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %></h1>
<br/>
<a href="hello-servlet">Hello Servlet</a>
</body>
</html>
```

### src\main\webapp\WEB-INF\web.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">
</web-app>
```

### src\test\java\org\example\dz_001\SupportServletTest.java
```java
package org.example.dz_001;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SupportServletTest {

  private MessageRepository repo;
  private SupportServlet servlet;

  @BeforeEach
  void setUp() {
    repo = spy(new MessageRepository("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"));
    servlet = new SupportServlet(repo);
  }

  @Test
  void testGet() throws Exception {
    doReturn(List.of("A", "B")).when(repo).findAll();
    StringWriter sw = new StringWriter();
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse res = mock(HttpServletResponse.class);
    when(res.getWriter()).thenReturn(new PrintWriter(sw));

    servlet.doGet(req, res);

    verify(res).setContentType(any());
    assertEquals("A\r\nB\r\n", sw.toString());
    verify(repo).findAll();
  }

  @Test
  void testPost() throws Exception {
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse res = mock(HttpServletResponse.class);
    when(req.getReader()).thenReturn(
            new BufferedReader(new StringReader("Новая фраза")));

    servlet.doPost(req, res);
    verify(repo).save("Новая фраза");
    verify(res).setStatus(HttpServletResponse.SC_CREATED);
  }
}
```

