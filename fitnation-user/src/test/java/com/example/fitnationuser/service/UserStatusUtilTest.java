package com.example.fitnationuser.service;

import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.UserBlockedException;
import com.example.fitnationcommon.exception.UserInactiveException;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserStatusUtilTest {

    private UserStatusUtil userStatusUtil;

    @BeforeEach
    void setUp() {
        userStatusUtil = new UserStatusUtil();
    }

    @Test
    void isBlockedOrInactive_returnsTrueForBlockedInactiveDeletedOrSoftDeleted() {
        assertTrue(userStatusUtil.isBlockedOrInactive(User.builder().status(UserStatus.BLOCKED).build()));
        assertTrue(userStatusUtil.isBlockedOrInactive(User.builder().status(UserStatus.INACTIVE).build()));
        assertTrue(userStatusUtil.isBlockedOrInactive(User.builder().status(UserStatus.DELETED).build()));
        assertTrue(userStatusUtil.isBlockedOrInactive(
                User.builder().status(UserStatus.ACTIVE).deletedAt(LocalDateTime.now()).build()));
    }

    @Test
    void isBlockedOrInactive_returnsFalseForActiveUserWithoutDeletion() {
        assertFalse(userStatusUtil.isBlockedOrInactive(User.builder().status(UserStatus.ACTIVE).build()));
    }

    @Test
    void ensureActive_throwsWhenDeleted() {
        User user = User.builder().status(UserStatus.ACTIVE).deletedAt(LocalDateTime.now()).build();

        assertThrows(UserBlockedException.class, () -> userStatusUtil.ensureActive(user));
    }

    @Test
    void ensureActive_throwsWhenStatusDeleted() {
        User user = User.builder().status(UserStatus.DELETED).build();

        assertThrows(UserBlockedException.class, () -> userStatusUtil.ensureActive(user));
    }

    @Test
    void ensureActive_throwsWhenBlocked() {
        User user = User.builder().status(UserStatus.BLOCKED).build();

        assertThrows(UserBlockedException.class, () -> userStatusUtil.ensureActive(user));
    }

    @Test
    void ensureActive_throwsWhenInactive() {
        User user = User.builder().status(UserStatus.INACTIVE).build();

        assertThrows(UserInactiveException.class, () -> userStatusUtil.ensureActive(user));
    }

    @Test
    void ensureActive_doesNotThrowForActiveUser() {
        User user = User.builder().status(UserStatus.ACTIVE).build();

        userStatusUtil.ensureActive(user);
    }
}
