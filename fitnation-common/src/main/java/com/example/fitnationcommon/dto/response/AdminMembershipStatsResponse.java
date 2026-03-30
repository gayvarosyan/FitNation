package com.example.fitnationcommon.dto.response;

import java.math.BigDecimal;

public record AdminMembershipStatsResponse(
        BigDecimal monthlyRecurringRevenue,
        long activeSubscriptions,
        double churnRate,
        long pastDueAccounts
) {}
