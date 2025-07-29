package org.example.dz_001.dispatcher;

import jakarta.servlet.http.HttpServletRequest;

public interface HandlerMapping {

  HandlerMethod lookup(HttpServletRequest req);
}
