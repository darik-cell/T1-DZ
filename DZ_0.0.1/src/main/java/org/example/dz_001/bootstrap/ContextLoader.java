package org.example.dz_001.bootstrap;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.dz_001.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;

@WebListener
public class ContextLoader implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ApplicationContext context = null;
    try {
      context = new ApplicationContext();
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    sce.getServletContext().setAttribute("context", context);
  }
}
