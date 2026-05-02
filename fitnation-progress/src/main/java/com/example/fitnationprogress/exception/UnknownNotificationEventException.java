package com.example.fitnationprogress.exception;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationprogress.enums.NotificationEventType;

public class UnknownNotificationEventException extends RuntimeException {

    public UnknownNotificationEventException(NotificationEventType eventType) {
        super(ApplicationConstants.NOTIFICATION_UNKNOWN_EVENT_PREFIX + eventType);
    }
}
