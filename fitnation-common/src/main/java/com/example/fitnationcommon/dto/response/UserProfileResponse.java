package com.example.fitnationcommon.dto.response;

public record UserProfileResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String role,
        String status
) {}

