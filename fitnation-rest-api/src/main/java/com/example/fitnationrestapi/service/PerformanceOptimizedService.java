package com.example.fitnationrestapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Slf4j
@Service
@Transactional
public abstract class PerformanceOptimizedService {

    protected <T> T executeWithMonitoring(String operationName, Supplier<T> operation) {
        long startTime = System.currentTimeMillis();
        try {
            T result = operation.get();
            long duration = System.currentTimeMillis() - startTime;

            if (duration > 1000) {
                log.warn("Slow database operation: {} took {} ms", operationName, duration);
            } else if (duration > 500) {
                log.info("Database operation: {} took {} ms", operationName, duration);
            }

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Database operation failed: {} after {} ms", operationName, duration, e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    protected <T> T executeReadOnlyWithMonitoring(String operationName, Supplier<T> operation) {
        return executeWithMonitoring(operationName, operation);
    }

    protected Pageable validatePageable(Pageable pageable) {
        int maxSize = 100;
        if (pageable.getPageSize() > maxSize) {
            log.warn("Page size {} exceeds maximum of {}, limiting to {}",
                    pageable.getPageSize(), maxSize, maxSize);
            return PageRequest.of(0, maxSize, pageable.getSort());
        }
        return pageable;
    }

    protected void logPaginationInfo(String operation, Page<?> page) {
        log.debug("Pagination info for {}: page {} of {}, total {} elements, {} total pages",
                operation,
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
