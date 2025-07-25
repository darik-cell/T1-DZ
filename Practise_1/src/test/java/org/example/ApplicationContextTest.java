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
