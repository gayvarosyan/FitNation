package com.example.fitnationcommon.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record MembershipTypeResponse(
        Long id,
        String name,
        @JsonProperty("duration") Integer durationDays,
        BigDecimal price,
        String description,
        Long nutritionPlanId,
        Long trainerId,
        Long groupClassId
) {}
