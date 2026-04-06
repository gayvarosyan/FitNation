package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.MembershipRequestStatus;

import java.time.Instant;

public record UserMembershipRequestResponse(
        Long id,
        Long membershipTypeId,
        String membershipTypeName,
        Integer durationDays,
        MembershipRequestStatus status,
        Instant createdAt,
        Instant reviewedAt,
        String rejectionReason
) {}
