package org.example.dispatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ApplicationContext;

import java.io.IOException;

public class DispatcherServlet extends HttpServlet {

  private ApplicationContext applicationContext;
  private MappingProvider mappingProvider;
  private HttpCallDispatcher httpCallDispatcher;

  @Override
  public void init() throws ServletException {
    applicationContext = new ApplicationContext("org.example.configuration");
    mappingProvider = new CommonMappingProvider(applicationContext);
    httpCallDispatcher = applicationContext.getInstance(HttpCallDispatcher.class);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    final var handler = mappingProvider.getMapping(req);
    httpCallDispatcher.dispatch(handler, resp);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doPost(req, resp);
  }
}
