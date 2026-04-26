package com.example.fitnationprogress.validation;

import com.example.fitnationprogress.dto.CreateUserProgressEntryRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserProgressValidatorTest {

    private final UserProgressValidator validator = new UserProgressValidator();

    @Test
    void validateForCreate_rejectsAllMetricsMissing() {
        CreateUserProgressEntryRequest request = new CreateUserProgressEntryRequest(
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                null,
                "note");

        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(request));
    }

    @Test
    void validateForCreate_acceptsAtLeastOneMetric() {
        CreateUserProgressEntryRequest request = new CreateUserProgressEntryRequest(
                LocalDateTime.now(),
                BigDecimal.valueOf(80),
                null,
                null,
                null,
                null,
                null,
                null);

        assertDoesNotThrow(() -> validator.validateForCreate(request));
    }
}
