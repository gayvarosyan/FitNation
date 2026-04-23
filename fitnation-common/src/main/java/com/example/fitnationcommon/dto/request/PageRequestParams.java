package com.example.fitnationcommon.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Set;

public class PageRequestParams {

    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_SIZE = 20;
    private static final int DEFAULT_PAGE = 0;

    public static Pageable toPageable(Integer page, Integer size, String sort, Set<String> allowedSortFields) {
        int p = page != null ? page : DEFAULT_PAGE;
        int s = size != null ? size : DEFAULT_SIZE;

        if (p < 0) {
            throw new IllegalArgumentException("page must be >= 0");
        }
        if (s <= 0 || s > MAX_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_SIZE);
        }

        Sort springSort = parseSort(sort, allowedSortFields);
        return PageRequest.of(p, s, springSort);
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