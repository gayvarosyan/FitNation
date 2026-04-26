package com.example.fitnationprogress.factory;

import com.example.fitnationprogress.constants.NotificationContextKeys;
import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationCommandFactoryTest {

    @Test
    void classBookedCarriesCoreContextKeys() {
        var cmd = NotificationCommandFactory.classBooked(
                10L, 20L, "Yoga", "2026-05-01", "10:00", "Alex P.");
        assertEquals(NotificationEventType.CLASS_BOOKED, cmd.eventType());
        assertEquals(NotificationEntityType.CLASS_BOOKING, cmd.entityType());
        assertEquals(10L, cmd.entityId());
        assertEquals("20", cmd.context().get(NotificationContextKeys.SUBJECT_USER_ID));
        assertEquals("Yoga", cmd.context().get(NotificationContextKeys.CLASS_NAME));
        assertEquals("Alex P.", cmd.context().get(NotificationContextKeys.TRAINER_NAME));
    }
}
