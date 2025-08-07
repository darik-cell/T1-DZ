package org.example;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dispatcher.DispatcherServlet;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DispatcherServletTest {

  @Test
  public void should_provide_support_phrase() throws Exception {
    final var stringWriter = new StringWriter();
    final var printWriter = new PrintWriter(stringWriter);
    final var request = mock(HttpServletRequest.class);
    when(request.getPathInfo()).thenReturn("/support");
    when(request.getMethod()).thenReturn("GET");
    final var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(printWriter);
    final var dispatcherServlet = new DispatcherServlet();

    dispatcherServlet.init();
    dispatcherServlet.doGet(request, response);

    assertEquals("""
            {"phrase":"Hey!"}""",  stringWriter.toString());
  }

  @Test
  public void should_set_new_phrase() throws Exception {
    final var requestContent = """
            {"phrase":"Hey!"}""";
    final var stringWriter = new StringWriter();
    final var printWriter = new PrintWriter(stringWriter);
    final var request = mock(HttpServletRequest.class);
    when(request.getPathInfo()).thenReturn("/support");
    when(request.getMethod()).thenReturn("POST");
    final var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(printWriter);
    when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestContent)));
    final var dispatcherServlet = new DispatcherServlet();

    dispatcherServlet.init();
    dispatcherServlet.doPost(request, response);

    verify(response, times(1)).setStatus(201);
  }
}
