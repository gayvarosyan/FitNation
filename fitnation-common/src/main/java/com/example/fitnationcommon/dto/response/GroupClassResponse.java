package com.example.fitnationcommon.dto.response;

public record GroupClassResponse(
        Long id,
        String name,
        String description,
        Integer capacity,
        Long trainerId
) {}
