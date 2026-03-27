package com.example.fitnationcommon.dto.request;

public record PurchaseMembershipRequest(
        Long membershipTypeId,
        Long nutritionPlanId,
        Long trainerId,
        Long groupClassId
) {}
