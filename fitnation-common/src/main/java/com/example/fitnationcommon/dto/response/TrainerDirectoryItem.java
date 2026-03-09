package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.UserStatus;

public record TrainerDirectoryItem(
        String trainerId,
        String firstName,
        String lastName,
        String specialization,
        String bio,
        String email,
        String phone,
        UserStatus status
) {}
