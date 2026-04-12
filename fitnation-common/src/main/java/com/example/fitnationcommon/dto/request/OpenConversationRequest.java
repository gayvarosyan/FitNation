package com.example.fitnationcommon.dto.request;

public record OpenConversationRequest(
        Long trainerId,
        Long clientId
) {}