package org.example.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.stream.Collectors;

public class JsonContentTypeHttpCallDispatcher implements HttpCallDispatcher {

  private final ObjectMapper objectMapper;

  public JsonContentTypeHttpCallDispatcher(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void dispatch(CommonMappingProvider.Handler controller, HttpServletRequest req, HttpServletResponse resp) {
    try {
      final Object result;
      resp.setContentType("application/json");
      if (controller.method().getParameterCount() == 0) {
        result = controller.method().invoke(controller.target());
        resp.getWriter().append(objectMapper.writeValueAsString(result));
      }
      else {
        final var parameterType = controller.method().getParameters()[0].getType();
        String content = req.getReader().lines().collect(Collectors.joining());
        controller.method().invoke(controller.target(), objectMapper.readValue(content, parameterType));
        resp.setStatus(201);
      }
    }
    catch (Exception e) {}
  }
}
