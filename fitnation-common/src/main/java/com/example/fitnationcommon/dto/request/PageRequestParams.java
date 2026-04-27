package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.exception.InvalidFilterException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Set;

public class PageRequestParams {

    public static Pageable toPageable(Integer page, Integer size, String sort, Set<String> allowedSortFields) {
        int pageNumber = page != null ? page : ApplicationConstants.PAGINATION_DEFAULT_PAGE;
        int pageSize = size != null ? size : ApplicationConstants.PAGINATION_DEFAULT_SIZE;

        if (pageNumber < 0) {
            throw new InvalidFilterException(ApplicationConstants.INVALID_PAGE_NUMBER);
        }
        if (pageSize <= 0 || pageSize > ApplicationConstants.PAGINATION_MAX_SIZE) {
            throw new InvalidFilterException(ApplicationConstants.INVALID_PAGE_SIZE + ApplicationConstants.PAGINATION_MAX_SIZE);
        }

        Sort springSort = parseSort(sort, allowedSortFields);
        return PageRequest.of(pageNumber, pageSize, springSort);
    }

    private static Sort parseSort(String sort, Set<String> allowedFields) {
        if (sort == null || sort.isBlank()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();

        if (!allowedFields.isEmpty() && !allowedFields.contains(field)) {
            throw new IllegalArgumentException(
                    "Invalid sort field '" + field + "'. Allowed: " + allowedFields);
        }

        Sort.Direction direction = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, field);
    }
}