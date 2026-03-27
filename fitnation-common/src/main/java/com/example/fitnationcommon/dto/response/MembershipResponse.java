package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.MembershipStatus;
import java.time.LocalDate;

public record MembershipResponse(
        Long id,
        String membershipType,
        Long membershipTypeId,
        LocalDate startDate,
        LocalDate endDate,
        MembershipStatus status,
        Long nutritionPlanId,
        Long trainerId,
        Long groupClassId
) {}
