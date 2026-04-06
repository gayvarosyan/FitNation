package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.MembershipRequestStatus;

import java.time.Instant;

public record AdminMembershipRequestResponse(
        Long id,
        Long userId,
        String userEmail,
        String userFirstName,
        String userLastName,
        Long membershipTypeId,
        String membershipTypeName,
        Integer durationDays,
        MembershipRequestStatus status,
        Instant createdAt,
        Instant reviewedAt,
        Long reviewedByUserId,
        String rejectionReason
) {}
