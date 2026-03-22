package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.MembershipStatus;
import java.time.LocalDate;

public record MembershipResponse(
        Long id,
        String membershipType,
        LocalDate startDate,
        LocalDate endDate,
        MembershipStatus status
) {}
