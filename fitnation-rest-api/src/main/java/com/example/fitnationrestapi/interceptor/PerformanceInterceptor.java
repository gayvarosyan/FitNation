package com.example.fitnationrestapi.interceptor;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class PerformanceInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex) {

        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String uri = request.getRequestURI();
            String method = request.getMethod();

            if (duration > 2000) {
                log.warn("Slow API request: {} {} took {} ms", method, uri, duration);
            } else if (duration > 1000) {
                log.info("API request: {} {} took {} ms", method, uri, duration);
            }

            if (uri.contains("/conversations") || uri.contains("/messages") ||
                    uri.contains("/bookings") || uri.contains("/memberships")) {
                log.debug("High-frequency endpoint accessed: {} {} in {} ms", method, uri, duration);
            }
        }
    }
}
