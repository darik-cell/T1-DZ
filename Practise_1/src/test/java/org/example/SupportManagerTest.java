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
