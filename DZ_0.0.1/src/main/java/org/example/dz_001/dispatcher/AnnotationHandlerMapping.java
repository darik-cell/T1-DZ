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
