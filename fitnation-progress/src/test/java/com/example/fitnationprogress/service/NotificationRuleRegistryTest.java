package com.example.fitnationprogress.service;

import com.example.fitnationprogress.enums.NotificationEventType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NotificationRuleRegistryTest {

    private final NotificationRuleRegistry registry = new NotificationRuleRegistry();

    @Test
    void everyDeclaredEventTypeHasAtLeastOneSegment() {
        Set<NotificationEventType> covered = EnumSet.noneOf(NotificationEventType.class);
        for (NotificationEventType type : NotificationEventType.values()) {
            assertFalse(registry.segmentsFor(type).isEmpty(), "Missing segments for " + type);
            covered.add(type);
        }
        assertFalse(covered.isEmpty());
    }

    @Test
    void classCanceledHasUserAndStaffSegments() {
        var segments = registry.segmentsFor(NotificationEventType.CLASS_CANCELED);
        long distinctAudiences = segments.stream().map(NotificationRuleSegment::audience).distinct().count();
        assertEquals(2, distinctAudiences);
    }
}
