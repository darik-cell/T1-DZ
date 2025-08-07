package org.example.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HttpCallDispatcher {
  void dispatch(CommonMappingProvider.Handler controller, HttpServletRequest req, HttpServletResponse resp);
}
