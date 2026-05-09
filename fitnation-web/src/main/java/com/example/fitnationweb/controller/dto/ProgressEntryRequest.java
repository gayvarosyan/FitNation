package com.example.fitnationweb.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProgressEntryRequest(
        @NotBlank(message = "Recorded date/time is required.")
        String recordedAt,

        @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0.")
        BigDecimal weight,

        @DecimalMin(value = "0.0", inclusive = true, message = "Body fat percent must be 0 or higher.")
        BigDecimal bodyFatPercent,

        @DecimalMin(value = "0.0", inclusive = true, message = "Muscle mass must be 0 or higher.")
        BigDecimal muscleMass,

        @DecimalMin(value = "0.0", inclusive = true, message = "Waist must be 0 or higher.")
        BigDecimal waistCm,

        @DecimalMin(value = "0.0", inclusive = true, message = "Chest must be 0 or higher.")
        BigDecimal chestCm,

        @DecimalMin(value = "0.0", inclusive = true, message = "Hip must be 0 or higher.")
        BigDecimal hipCm,

        @Size(max = 1000, message = "Notes are too long.")
        String notes
) {
}

