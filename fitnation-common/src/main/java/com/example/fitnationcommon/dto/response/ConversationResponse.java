package com.example.fitnationcommon.dto.response;
import java.time.LocalDateTime;

public record ConversationResponse(
        Long id,
        Long clientId,
        Long trainerId,
        LocalDateTime lastMessageAt,
        MessageResponse lastMessage
) {}