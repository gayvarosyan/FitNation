package com.example.fitnationrestapi.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
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

            if (duration > ApplicationConstants.DB_OPERATION_WARN_THRESHOLD_MS) {
                log.warn("Slow database operation: {} took {} ms", operationName, duration);
            } else if (duration > ApplicationConstants.DB_OPERATION_INFO_THRESHOLD_MS) {
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
        if (pageable.getPageSize() > ApplicationConstants.MAX_PAGE_SIZE) {
            log.warn(
                    "Page size {} exceeds maximum of {}, limiting to {}",
                    pageable.getPageSize(),
                    ApplicationConstants.MAX_PAGE_SIZE,
                    ApplicationConstants.MAX_PAGE_SIZE
            );

            return PageRequest.of(
                    pageable.getPageNumber(),
                    ApplicationConstants.MAX_PAGE_SIZE,
                    pageable.getSort()
            );
        }
        return pageable;
    }

    protected void logPaginationInfo(String operation, Page<?> page) {
        log.debug(
                "Pagination info for {}: page {} of {}, total {} elements",
                operation,
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}