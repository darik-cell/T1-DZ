Мне надо создать servlet, который будет выдавать подбадривающие сообщения по get /help-service/v1/support и post /help-service/v1/support который добавляет в список фраз новую, давай будем использовать h2 базу данных, использовать text/plain как content/type, использовать war и поднять tomcat с war архивом в docker-compose, написать тесты (junit, mockito)


```
.
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/
│   │   │       └── example/
│   │   │           └── dz_001/
│   │   │               └── HelloServlet.java
│   │   ├── resources/
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml
│   │       └── index.jsp
│   └── test/
│       ├── java/
│       └── resources/
├── build.gradle
├── DZ_0.0.1.iml
└── settings.gradle
```


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
}

sourceCompatibility = '17'
targetCompatibility = '17'

tasks.withType(JavaCompile) {
  options.encoding = 'UTF-8'
}

dependencies {
  compileOnly('jakarta.servlet:jakarta.servlet-api:6.1.0')

  testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

test {
useJUnitPlatform()}
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

### src\main\java\org\example\dz_001\HelloServlet.java
```java
package org.example.dz_001;

import java.io.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }
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

    <
</web-app>
```

