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
