package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.enums.MembershipStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateMembershipRequest(
        @NotNull Long membershipTypeId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull MembershipStatus status,
        Long nutritionPlanId,
        Long trainerId,
        Long groupClassId
) {}
