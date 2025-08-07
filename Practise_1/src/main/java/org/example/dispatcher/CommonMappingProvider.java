package org.example.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import org.example.ApplicationContext;
import org.example.controller.HttpMethod;
import org.example.controller.annotation.Controller;
import org.example.controller.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommonMappingProvider implements MappingProvider {

  private static final String BASE_PATH = "/support";

  private final ApplicationContext applicationContext;
  private final Map<Mapping, Handler> mapping2Handler = new HashMap<>();

  public CommonMappingProvider(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    init();
  }

  @Override
  public Handler getMapping(HttpServletRequest req) {
    return mapping2Handler.get(new Mapping(req.getPathInfo().substring(BASE_PATH.length()), HttpMethod.valueOf(req.getMethod())));
  }

  private void init() {
    final var controllers = applicationContext.getAllInstances().stream()
            .filter(it -> Arrays.stream(it.getClass().getInterfaces()).anyMatch(i -> i.isAnnotationPresent(Controller.class)))
            .toList();
    for (Object controller : controllers) {
      Arrays.stream(controller.getClass().getInterfaces())
              .flatMap(i -> Arrays.stream(i.getMethods()))
              .filter(it -> it.isAnnotationPresent(RequestMapping.class))
              .forEach(method -> addMapping(controller, method));
    }

  }

  private void addMapping(Object controller, Method method) {
    final var mappingAnnotation = method.getAnnotation(RequestMapping.class);
    mapping2Handler.put(new Mapping(mappingAnnotation.path(), mappingAnnotation.method()), new Handler(controller, method));
  }

  public record Mapping(
          String path,
          HttpMethod method
  ) {
  }

  public record Handler(
          Object target,
          Method method
  ) {
  }
}
