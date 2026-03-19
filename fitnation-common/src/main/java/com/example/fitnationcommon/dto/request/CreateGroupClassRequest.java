package com.example.fitnationcommon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateGroupClassRequest(
        @NotBlank
        String name,

        String description,

        @NotNull
        @Positive
        Integer capacity,

        @NotNull
        Long trainerId
) {}