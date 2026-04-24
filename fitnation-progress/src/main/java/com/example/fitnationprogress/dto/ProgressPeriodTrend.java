package com.example.fitnationprogress.dto;

public record ProgressPeriodTrend(
        int days,
        ProgressMetricDeltas deltas
) {
}
