package com.example.fitnationprogress.exception;

import com.example.fitnationcommon.constants.ApplicationConstants;

public class InAppNotificationNotFoundException extends RuntimeException {

    public InAppNotificationNotFoundException() {
        super(ApplicationConstants.IN_APP_NOTIFICATION_NOT_FOUND);
    }
}
