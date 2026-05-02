package com.example.fitnationprogress.dto;

import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public record NotificationTriggerCommand(
        NotificationEventType eventType,
        NotificationEntityType entityType,
        Long entityId,
        Map<String, String> context) {

    public NotificationTriggerCommand {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(entityId, "entityId");
        context = context == null ? Map.of() : Map.copyOf(context);
    }

    public static NotificationTriggerCommand of(
            NotificationEventType eventType,
            NotificationEntityType entityType,
            Long entityId,
            Map<String, String> context) {
        return new NotificationTriggerCommand(eventType, entityType, entityId, context);
    }

    public Map<String, String> contextView() {
        return Collections.unmodifiableMap(context);
    }
}
