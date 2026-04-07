package com.example.fitnationcommon.api;

import com.example.fitnationcommon.dto.response.ErrorResponse;
import com.example.fitnationcommon.enums.ErrorCode;

import java.time.Instant;

public final class StandardErrorResponses {

    private StandardErrorResponses() {}

    public static ErrorResponse unauthorized(String path) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(401)
                .error("Unauthorized")
                .code(ErrorCode.UNAUTHORIZED.name())
                .message("Authentication required.")
                .path(path)
                .details(null)
                .build();
    }

    public static ErrorResponse accessForbidden(String path) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(403)
                .error("Forbidden")
                .code(ErrorCode.FORBIDDEN.name())
                .message("You do not have permission to access this resource.")
                .path(path)
                .details(null)
                .build();
    }
}
