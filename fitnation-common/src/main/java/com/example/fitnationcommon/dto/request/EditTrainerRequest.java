package com.example.fitnationcommon.dto.request;

public record EditTrainerRequest(
        String firstName,
        String lastName,
        String phone,
        String specialization
) {}