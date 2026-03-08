package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EditTrainerRequest(
        @Size(max = ApplicationConstants.SMALL_TEXT)
        String firstName,

        @Size(max = ApplicationConstants.SMALL_TEXT)
        String lastName,

        @Pattern(
                regexp = ApplicationConstants.PASSWORD_REGEX,
                message = ApplicationConstants.VALID_PASSWORD_MESSAGE
        )
        String password,

        @Size(max = ApplicationConstants.SMALL_TEXT)
        String phone,

        @Size(max = ApplicationConstants.SMALL_TEXT)
        String specialization,

        @Size(max = ApplicationConstants.LARGE_TEXT)
        String bio
) {}