package com.example.fitnationprogress.service;

import com.example.fitnationprogress.dto.NotificationTriggerCommand;
import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationTemplateRendererTest {

    private final NotificationTemplateRenderer renderer = new NotificationTemplateRenderer();

    @Test
    void replacesPlaceholders() {
        var command = NotificationTriggerCommand.of(
                NotificationEventType.CLASS_BOOKED,
                NotificationEntityType.CLASS_BOOKING,
                1L,
                Map.of("className", "Spin", "scheduleDate", "2026-04-26"));
        assertEquals("Spin on 2026-04-26", renderer.render("{{className}} on {{scheduleDate}}", command));
    }

    @Test
    void missingPlaceholderBecomesEmpty() {
        var command = NotificationTriggerCommand.of(
                NotificationEventType.CLASS_BOOKED,
                NotificationEntityType.CLASS_BOOKING,
                1L,
                Map.of());
        assertEquals("X", renderer.render("{{missing}}X", command));
    }
}
