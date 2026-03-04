package com.example.fitnationcommon.dto.response;

public record ErrorResponse(
        int status,
        String message
) {}
