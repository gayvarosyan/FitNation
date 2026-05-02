package com.example.fitnationprogress.service;

import com.example.fitnationprogress.enums.NotificationEventType;
import com.example.fitnationprogress.enums.NotificationSeverity;

record NotificationRuleSegment(
        NotificationEventType eventType,
        RecipientAudience audience,
        NotificationSeverity severity,
        String titleTemplate,
        String bodyTemplate) {
}
