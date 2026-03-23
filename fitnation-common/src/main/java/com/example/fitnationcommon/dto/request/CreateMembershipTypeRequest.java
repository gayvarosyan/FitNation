package com.example.fitnationcommon.dto.request;

import java.math.BigDecimal;

public record CreateMembershipTypeRequest(
        String name,
        Integer durationDays,
        BigDecimal price,
        String description
) {}
