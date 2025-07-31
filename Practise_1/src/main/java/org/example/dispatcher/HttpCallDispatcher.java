package org.example.dispatcher;

import jakarta.servlet.http.HttpServletResponse;

public interface HttpCallDispatcher {
  void dispatch(CommonMappingProvider.Handler controller, HttpServletResponse resp);
}
