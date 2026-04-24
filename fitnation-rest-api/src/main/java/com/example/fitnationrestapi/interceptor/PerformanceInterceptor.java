package com.example.fitnationrestapi.interceptor;

import com.example.fitnationcommon.constants.ApplicationConstants;
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
        if (startTime == null) {
            return;
        }

        long duration = System.currentTimeMillis() - startTime;
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (duration > ApplicationConstants.SLOW_API_THRESHOLD_MS) {
            log.warn(
                    "Slow API request: {} {} took {} ms",
                    method,
                    uri,
                    duration
            );
        } else if (duration > ApplicationConstants.INFO_API_THRESHOLD_MS) {
            log.info(
                    "API request: {} {} took {} ms",
                    method,
                    uri,
                    duration
            );
        }

        for (String endpoint : ApplicationConstants.HIGH_FREQUENCY_ENDPOINTS) {
            if (uri.contains(endpoint)) {
                log.debug(
                        "High-frequency endpoint accessed: {} {} in {} ms",
                        method,
                        uri,
                        duration
                );
                break;
            }
        }
    }
}
