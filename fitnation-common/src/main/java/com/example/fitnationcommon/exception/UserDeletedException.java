package com.example.fitnationcommon.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeletedException extends RuntimeException {

    private final Long userId;

    public UserDeletedException(Long userId) {
        super("User account has been deactivated [userId=" + userId + "]" );
        this.userId = userId;
    }
}