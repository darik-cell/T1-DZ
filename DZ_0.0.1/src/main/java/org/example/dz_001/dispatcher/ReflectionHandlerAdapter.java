package org.example.dz_001.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ReflectionHandlerAdapter implements HandlerAdapter {
  @Override
  public boolean supports(HandlerMethod hm) {
    Class<?>[] p = hm.method().getParameterTypes();
    return p.length == 2
            && HttpServletRequest.class.isAssignableFrom(p[0])
            && HttpServletResponse.class.isAssignableFrom(p[1]);
  }

  @Override
  public void handle(HttpServletRequest req, HttpServletResponse resp, HandlerMethod hm) throws Exception {
    hm.method().invoke(hm.bean(), req, resp);
  }
}
