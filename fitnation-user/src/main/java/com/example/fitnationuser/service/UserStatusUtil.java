package com.example.fitnationuser.service;

import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.UserBlockedException;
import com.example.fitnationcommon.exception.UserInactiveException;
import com.example.fitnationuser.user.User;
import org.springframework.stereotype.Service;

@Service
public class UserStatusUtil {

    public boolean isBlockedOrInactive(User user) {
        return user.getStatus() == UserStatus.BLOCKED
                || user.getStatus() == UserStatus.INACTIVE;
    }

    public void ensureActive(User user) {
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException("User is blocked");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UserInactiveException("User is inactive");
        }
    }
}

