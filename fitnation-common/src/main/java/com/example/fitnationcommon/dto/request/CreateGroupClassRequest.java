package com.example.fitnationcommon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateGroupClassRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @NotNull(message = "Capacity is required")
        @Positive(message = "Capacity must be at least 1")
        Integer capacity,

        @NotNull(message = "Trainer id is required")
        Long trainerId
) {}