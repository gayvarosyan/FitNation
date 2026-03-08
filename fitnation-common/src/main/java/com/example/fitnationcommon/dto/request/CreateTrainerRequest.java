package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTrainerRequest(
        @NotBlank
        @Size(max = ApplicationConstants.SMALL_TEXT)
        String firstName,

        @NotBlank
        @Size(max = ApplicationConstants.SMALL_TEXT)
        String lastName,

        @NotBlank
        @Email(message = ApplicationConstants.VALID_EMAIL_MESSAGE)
        @Pattern(regexp = ApplicationConstants.EMAIL_REGEX)
        String email,

        @NotBlank
        @Pattern(
                regexp = ApplicationConstants.PASSWORD_REGEX,
                message = ApplicationConstants.VALID_PASSWORD_MESSAGE
        )
        String password,

        @NotBlank
        @Size(max = ApplicationConstants.SMALL_TEXT)
        String phone,

        @Size(max = ApplicationConstants.SMALL_TEXT)
        String specialization,

        @Size(max = ApplicationConstants.LARGE_TEXT)
        String bio
) {}
