package com.example.fitnationcommon.dto.response;


public record AuthResponse(
        Long id,
        String email,
        String role,
        String status,
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn
) {}