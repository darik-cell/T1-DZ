package org.example.dz_001;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "supportServlet", urlPatterns = "/v1/support")
public class SupportServlet extends HttpServlet {

  private final MessageRepository repo;

  public SupportServlet() {
    this.repo = new MessageRepository();
  }

  SupportServlet(MessageRepository repo) {
    this.repo = repo;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain; charset=utf-8");
    List<String> messages = repo.findAll();
    try (PrintWriter out = resp.getWriter()) {
      for (String m : messages) out.println(m);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String body = req.getReader().lines().collect(Collectors.joining("\n")).trim();
    if (body.isEmpty()) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Body is empty");
      return;
    }
    repo.save(body);
    resp.setStatus(HttpServletResponse.SC_CREATED);
  }
}
