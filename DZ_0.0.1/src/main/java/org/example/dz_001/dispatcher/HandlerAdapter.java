package org.example.dz_001.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
  boolean supports(HandlerMethod handlerMethod);
  void handle(HttpServletRequest req, HttpServletResponse resp, HandlerMethod hm) throws Exception;
}
