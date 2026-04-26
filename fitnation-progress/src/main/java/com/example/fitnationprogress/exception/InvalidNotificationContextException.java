package com.example.fitnationprogress.exception;

import com.example.fitnationcommon.constants.ApplicationConstants;

public class InvalidNotificationContextException extends RuntimeException {

    public InvalidNotificationContextException(String detail) {
        super(ApplicationConstants.NOTIFICATION_INVALID_CONTEXT_PREFIX + detail);
    }
}
