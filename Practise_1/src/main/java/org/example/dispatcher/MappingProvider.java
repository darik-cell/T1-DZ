package org.example.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dispatcher.CommonMappingProvider.Handler;

public interface MappingProvider {
  Handler getMapping(HttpServletRequest req);
}
