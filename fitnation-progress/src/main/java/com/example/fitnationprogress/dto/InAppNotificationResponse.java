package com.example.fitnationprogress.dto;

import com.example.fitnationprogress.enums.NotificationEventType;
import com.example.fitnationprogress.enums.NotificationSeverity;

import java.time.Instant;

public record InAppNotificationResponse(
        Long id,
        Long recipientUserId,
        NotificationEventType type,
        String title,
        String body,
        boolean read,
        Instant createdAt,
        String metadataJson,
        NotificationSeverity severity) {
}
