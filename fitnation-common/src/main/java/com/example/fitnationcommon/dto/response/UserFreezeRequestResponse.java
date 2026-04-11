package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.FreezeRequestStatus;
import com.example.fitnationcommon.enums.MembershipStatus;

import java.time.Instant;
import java.time.LocalDate;

public record UserFreezeRequestResponse(
        Long id, Long membershipId, String membershipTypeName,

        LocalDate membershipStartDate, LocalDate membershipEndDate, MembershipStatus membershipStatus,

        LocalDate freezeStart, LocalDate freezeEnd, FreezeRequestStatus status,

        Instant createdAt, Instant reviewedAt, String rejectionReason) {}

