package com.example.fitnationuser.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.InvalidPasswordException;
import com.example.fitnationcommon.exception.UserBlockedException;
import com.example.fitnationcommon.exception.UserInactiveException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import com.example.fitnationuser.validation.SoftDeleteValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserStatusUtil userStatusUtil;
    @Mock
    private SoftDeleteValidationService softDeleteValidationService;

    @InjectMocks
    private UserAuthService userAuthService;

    @Test
    void login_throwsWhenPasswordDoesNotMatch() {
        User user = User.builder()
                .email("u@test.com")
                .password("hash")
                .status(UserStatus.ACTIVE)
                .build();
        when(userRepository.findByEmailAndDeletedAtIsNull("u@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () ->
                userAuthService.login("u@test.com", "wrong"));
        verify(userStatusUtil, never()).ensureActive(any());
    }

    @Test
    void login_activatesPendingUserAndPersists() {
        User pending = User.builder()
                .id(7L)
                .email("p@test.com")
                .password("hash")
                .status(UserStatus.PENDING)
                .build();
        when(userRepository.findByEmailAndDeletedAtIsNull("p@test.com")).thenReturn(Optional.of(pending));
        when(passwordEncoder.matches("Valid1@pass", "hash")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(userStatusUtil).ensureActive(pending);

        User result = userAuthService.login("p@test.com", "Valid1@pass");

        assertEquals(UserStatus.ACTIVE, result.getStatus());
        verify(userRepository).save(pending);
    }

    @Test
    void login_returnsExistingUserWhenAlreadyActive() {
        User active = User.builder()
                .id(8L)
                .email("a@test.com")
                .password("hash")
                .status(UserStatus.ACTIVE)
                .build();
        when(userRepository.findByEmailAndDeletedAtIsNull("a@test.com")).thenReturn(Optional.of(active));
        when(passwordEncoder.matches("Valid1@pass", "hash")).thenReturn(true);
        doNothing().when(userStatusUtil).ensureActive(active);

        User result = userAuthService.login("a@test.com", "Valid1@pass");

        assertSame(active, result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_propagatesBlockedWhenStatusCheckFails() {
        User user = User.builder()
                .email("b@test.com")
                .password("hash")
                .status(UserStatus.BLOCKED)
                .build();
        when(userRepository.findByEmailAndDeletedAtIsNull("b@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Valid1@pass", "hash")).thenReturn(true);
        doThrow(new UserBlockedException(ApplicationConstants.USER_BLOCKED))
                .when(userStatusUtil).ensureActive(user);

        assertThrows(UserBlockedException.class, () ->
                userAuthService.login("b@test.com", "Valid1@pass"));
    }

    @Test
    void login_propagatesInactive() {
        User user = User.builder()
                .email("i@test.com")
                .password("hash")
                .status(UserStatus.INACTIVE)
                .build();
        when(userRepository.findByEmailAndDeletedAtIsNull("i@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Valid1@pass", "hash")).thenReturn(true);
        doThrow(new UserInactiveException(ApplicationConstants.USER_INACTIVE))
                .when(userStatusUtil).ensureActive(user);

        assertThrows(UserInactiveException.class, () ->
                userAuthService.login("i@test.com", "Valid1@pass"));
    }

    @Test
    void findByEmail_throwsWhenMissing() {
        when(userRepository.findByEmailAndDeletedAtIsNull("nope@test.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () ->
                userAuthService.findByEmail("nope@test.com"));
    }
}
