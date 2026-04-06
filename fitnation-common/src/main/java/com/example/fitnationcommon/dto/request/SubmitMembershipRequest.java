package com.example.fitnationcommon.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubmitMembershipRequest(
        @NotNull Long membershipTypeId
) {}
