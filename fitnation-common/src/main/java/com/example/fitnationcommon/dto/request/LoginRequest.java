package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Email cannot be blank")
        @Email(message = ApplicationConstants.VALID_EMAIL_MESSAGE)
        String email,

        @NotBlank(message = "Password cannot be blank")
        String password
) {}