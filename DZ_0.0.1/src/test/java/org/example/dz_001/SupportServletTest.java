package org.example.dz_001;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SupportServletTest {

  private MessageRepository repo;
  private SupportServlet servlet;

  @BeforeEach
  void setUp() {
    repo = spy(new MessageRepository("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"));
    servlet = new SupportServlet(repo);
  }

  @Test
  void testGet() throws Exception {
    doReturn(List.of("A", "B")).when(repo).findAll();
    StringWriter sw = new StringWriter();
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse res = mock(HttpServletResponse.class);
    when(res.getWriter()).thenReturn(new PrintWriter(sw));

    servlet.doGet(req, res);

    verify(res).setContentType(any());
    assertEquals("A\r\nB\r\n", sw.toString());
    verify(repo).findAll();
  }

  @Test
  void testPost() throws Exception {
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse res = mock(HttpServletResponse.class);
    when(req.getReader()).thenReturn(
            new BufferedReader(new StringReader("Новая фраза")));

    servlet.doPost(req, res);
    verify(repo).save("Новая фраза");
    verify(res).setStatus(HttpServletResponse.SC_CREATED);
  }
}
