package com.example.fitnationcommon.dto.response;


public record AuthResponse(
        Long id,
        String email,
        String role,
        String message,
        String redirectUrl,
        String token
) {}