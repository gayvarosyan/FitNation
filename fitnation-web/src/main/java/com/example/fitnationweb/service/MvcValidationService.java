package com.example.fitnationweb.service;

import org.springframework.stereotype.Service;

@Service
public class MvcValidationService {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    public ValidationResult validatePagination(Integer page, Integer size) {
        if (page == null || size == null) {
            return ValidationResult.invalid("Invalid pagination parameters.");
        }
        if (page < 0) {
            return ValidationResult.invalid("Page must be 0 or greater.");
        }
        if (size < 1 || size > MAX_SIZE) {
            return ValidationResult.invalid("Size must be between 1 and " + MAX_SIZE + ".");
        }
        return ValidationResult.ok();
    }

    public int normalizePage(Integer page) {
        return Math.max(0, page == null ? DEFAULT_PAGE : page);
    }

    public int normalizeSize(Integer size) {
        return Math.max(1, Math.min(size == null ? DEFAULT_SIZE : size, MAX_SIZE));
    }

    public record ValidationResult(boolean valid, String message) {
        public static ValidationResult ok() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message == null ? "Invalid request." : message);
        }

        public boolean isInvalid() {
            return !valid;
        }
    }
}

