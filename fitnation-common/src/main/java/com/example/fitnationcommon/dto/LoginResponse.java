package com.example.fitnationcommon.dto;

public record LoginResponse(
        Long id,
        String email,
        String role,
        String status
) {}