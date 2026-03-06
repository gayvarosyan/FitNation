package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTrainerRequest(
        @Size(max = ApplicationConstants.SMALL_TEXT)
        String firstName,

        @Size(max = ApplicationConstants.SMALL_TEXT)
        String lastName,

        @Size(max = ApplicationConstants.SMALL_TEXT)
        String phone,

        @Size(max = ApplicationConstants.SMALL_TEXT)
        String specialization,

        @Size(max = ApplicationConstants.LARGE_TEXT)
        String bio,

        @NotNull
        UserStatus status
) {}
