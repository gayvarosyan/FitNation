package com.example.fitnationcommon.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        int status,
        String message,
        String code,
        Object details,
        Instant timestamp,
        String error,
        String path
) {}
