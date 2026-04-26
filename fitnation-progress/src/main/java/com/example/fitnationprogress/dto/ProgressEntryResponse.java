package com.example.fitnationprogress.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProgressEntryResponse(
        Long id,
        Long userId,
        LocalDateTime recordedAt,
        BigDecimal weight,
        BigDecimal bodyFatPercent,
        BigDecimal muscleMass,
        BigDecimal waistCm,
        BigDecimal chestCm,
        BigDecimal hipCm,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
