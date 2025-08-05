package org.example.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

public class JsonContentTypeHttpCallDispatcher implements HttpCallDispatcher {

  private final ObjectMapper objectMapper;

  public JsonContentTypeHttpCallDispatcher(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void dispatch(CommonMappingProvider.Handler controller, HttpServletResponse resp) {
    try {
      final var result = controller.method().invoke(controller.target());
      resp.setContentType("application/json");
      resp.getWriter().append(objectMapper.writeValueAsString(result));
    }
    catch (Exception e) {}
  }
}
