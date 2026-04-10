package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.FreezeRequestStatus;

import java.time.Instant;
import java.time.LocalDate;

public record AdminFreezeRequestResponse(

        Long id, Long membershipId, String membershipTypeName,

        Long userId, String userFirstName, String userLastName, String userEmail,

        LocalDate freezeStart, LocalDate freezeEnd, FreezeRequestStatus status,

        Instant createdAt, Long reviewedById, Instant reviewedAt, String rejectionReason) {}
