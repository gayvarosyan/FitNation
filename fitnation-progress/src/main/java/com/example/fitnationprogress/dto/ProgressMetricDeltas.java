package com.example.fitnationprogress.dto;

import java.math.BigDecimal;

public record ProgressMetricDeltas(
        BigDecimal weightDelta,
        BigDecimal bodyFatPercentDelta,
        BigDecimal muscleMassDelta,
        BigDecimal waistCmDelta,
        BigDecimal chestCmDelta,
        BigDecimal hipCmDelta
) {
}
