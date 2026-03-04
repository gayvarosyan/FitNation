package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record LoginRequest(

        //sonq e krnas tanis constant

        @NotBlank(message = "Email cannot be blank")
        @Email(message = ApplicationConstants.VALID_EMAIL_MESSAGE)
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Pattern(
                regexp = ApplicationConstants.PASSWORD_REGEX,
                message = ApplicationConstants.VALID_PASSWORD_MESSAGE
        )
        String password
) {}