package com.example.fitnationuser.service;

import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void softDeleteUser_throwsWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userAdminService.softDeleteUser(99L));
    }

    @Test
    void softDeleteUser_setsDeletedStateAndPersists() {
        User user = User.builder()
                .id(4L)
                .status(UserStatus.ACTIVE)
                .build();
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userAdminService.softDeleteUser(4L);

        ArgumentCaptor<User> savedCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedCaptor.capture());
        User saved = savedCaptor.getValue();
        assertEquals(UserStatus.DELETED, saved.getStatus());
        assertNotNull(saved.getDeletedAt());
    }

    @Test
    void restoreUser_throwsWhenDeletedUserMissing() {
        when(userRepository.findByIdAndDeletedAtIsNotNull(15L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userAdminService.restoreUser(15L));
    }

    @Test
    void restoreUser_clearsDeletedStateAndPersists() {
        User user = User.builder()
                .id(6L)
                .status(UserStatus.DELETED)
                .deletedAt(LocalDateTime.now().minusDays(3))
                .build();
        when(userRepository.findByIdAndDeletedAtIsNotNull(6L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userAdminService.restoreUser(6L);

        ArgumentCaptor<User> savedCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedCaptor.capture());
        User saved = savedCaptor.getValue();
        assertEquals(UserStatus.ACTIVE, saved.getStatus());
        assertNull(saved.getDeletedAt());
    }
}
