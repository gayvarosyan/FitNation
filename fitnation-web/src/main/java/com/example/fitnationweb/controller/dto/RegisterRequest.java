package com.example.fitnationweb.controller.dto;

import com.example.fitnationcommon.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name is required.")
        @Size(max = 100, message = "First name is too long.")
        String firstName,

        @NotBlank(message = "Last name is required.")
        @Size(max = 100, message = "Last name is too long.")
        String lastName,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email format is invalid.")
        @Size(max = 255, message = "Email is too long.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
        String password,

        @NotBlank(message = "Phone is required.")
        @Size(max = 30, message = "Phone is too long.")
        String phone,

        @NotNull(message = "Role is required.")
        UserRole role,

        @Size(max = 255, message = "Specialization is too long.")
        String specialization,

        @Size(max = 1000, message = "Bio is too long.")
        String bio
) {
}

