package com.example.fitnationcommon.dto.response;
import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long senderId,
        String body,
        LocalDateTime createdAt
) {}