package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdminMembershipRecordResponse(
        Long id,
        Long userId,
        String userFirstName,
        String userLastName,
        String userEmail,
        Long membershipTypeId,
        String membershipTypeName,
        Integer membershipTypeDurationDays,
        BigDecimal membershipTypePrice,
        LocalDate startDate,
        LocalDate endDate,
        MembershipStatus status,
        Long nutritionPlanId,
        Long trainerId,
        Long groupClassId
) {}
