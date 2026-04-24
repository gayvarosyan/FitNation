package com.example.fitnationprogress.dto;

public record ProgressSummaryResponse(
        ProgressEntryResponse latestEntry,
        ProgressMetricDeltas changeVsPrevious,
        ProgressPeriodTrend trendLast7Days,
        ProgressPeriodTrend trendLast30Days,
        long totalEntries
) {
}
