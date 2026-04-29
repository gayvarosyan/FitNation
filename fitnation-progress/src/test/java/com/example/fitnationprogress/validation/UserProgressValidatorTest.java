package com.example.fitnationprogress.validation;

import com.example.fitnationprogress.dto.UpsertUserProgressEntryRequest;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserProgressValidatorTest {

    private final UserProgressValidator validator = new UserProgressValidator();

    @Test
    void validateEntry_rejectsAllMetricsMissing() {
        UpsertUserProgressEntryRequest request = new UpsertUserProgressEntryRequest(
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                null,
                "note");

        assertThrows(ValidationException.class, () -> validator.validateEntry(request));
    }

    @Test
    void validateEntry_acceptsAtLeastOneMetric() {
        UpsertUserProgressEntryRequest request = new UpsertUserProgressEntryRequest(
                LocalDateTime.now(),
                BigDecimal.valueOf(80),
                null,
                null,
                null,
                null,
                null,
                null);

        assertDoesNotThrow(() -> validator.validateEntry(request));
    }

    @Test
    void validateEntry_rejectsFutureRecordedAt() {
        UpsertUserProgressEntryRequest request = new UpsertUserProgressEntryRequest(
                LocalDateTime.now().plusHours(2),
                BigDecimal.valueOf(80),
                null,
                null,
                null,
                null,
                null,
                null);

        assertThrows(ValidationException.class, () -> validator.validateEntry(request));
    }
}
