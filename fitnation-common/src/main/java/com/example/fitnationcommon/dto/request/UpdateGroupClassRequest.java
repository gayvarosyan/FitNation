package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateGroupClassRequest(
        @NotBlank(message = ApplicationConstants.NAME_IS_REQUIRED)
        @Size(max = ApplicationConstants.SMALL_TEXT)
        String name,

        @Size(max = ApplicationConstants.LARGE_TEXT)
        String description,

        @NotNull(message = ApplicationConstants.CAPACITY_IS_REQUIRED)
        @Min(1)
        Integer capacity,

        @NotNull(message = ApplicationConstants.TRAINER_IS_REQUIRED)
        Long trainerId
) {}
