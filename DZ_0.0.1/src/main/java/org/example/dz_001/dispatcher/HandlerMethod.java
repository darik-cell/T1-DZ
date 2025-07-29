package org.example.dz_001.dispatcher;

import java.lang.reflect.Method;

public record HandlerMethod(
        Method method,
        Object bean,
        String path,
        String http
) {
}
