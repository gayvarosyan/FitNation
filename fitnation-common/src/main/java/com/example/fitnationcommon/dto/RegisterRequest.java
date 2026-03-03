package com.example.fitnationcommon.dto;

import com.example.fitnationcommon.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Size(max = 50)
        String firstName,

        @NotBlank
        @Size(max = 50)
        String lastName,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        @Size(max = 50)
        String phone,

        UserRole role,

        @Size(max = 50)
        String specialization,

        @Size(max = 250)
        String bio
) {}