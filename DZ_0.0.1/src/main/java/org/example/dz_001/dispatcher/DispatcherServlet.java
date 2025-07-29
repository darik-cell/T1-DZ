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
