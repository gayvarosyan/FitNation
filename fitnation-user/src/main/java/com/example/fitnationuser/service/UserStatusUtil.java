package com.example.fitnationuser.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.UserBlockedException;
import com.example.fitnationcommon.exception.UserDeletedException;
import com.example.fitnationcommon.exception.UserInactiveException;
import com.example.fitnationuser.user.User;
import org.springframework.stereotype.Service;

@Service
public class UserStatusUtil {

    public boolean isBlockedOrInactive(User user) {
        return user.getStatus() == UserStatus.BLOCKED
                || user.getStatus() == UserStatus.INACTIVE
                || user.getStatus() == UserStatus.DELETED
                || user.getDeletedAt() != null;
    }

    public void ensureActive(User user) {
        if (user.getDeletedAt() != null || user.getStatus() == UserStatus.DELETED) {
            throw new UserDeletedException(user.getId());
        }
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException(ApplicationConstants.USER_BLOCKED);
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UserInactiveException(ApplicationConstants.USER_INACTIVE);
        }
    }
}