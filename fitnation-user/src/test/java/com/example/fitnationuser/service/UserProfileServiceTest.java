package com.example.fitnationuser.service;

import com.example.fitnationcommon.dto.response.UserProfileResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void getProfile_throwsWhenUserNotFound() {
        when(userRepository.findByEmailAndDeletedAtIsNull("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userProfileService.getProfile("missing@test.com"));
    }

    @Test
    void getProfile_returnsResponseWhenUserExists() {
        User user = User.builder()
                .id(3L)
                .email("client@test.com")
                .firstName("C")
                .lastName("L")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();
        when(userRepository.findByEmailAndDeletedAtIsNull("client@test.com")).thenReturn(Optional.of(user));

        UserProfileResponse profile = userProfileService.getProfile("client@test.com");

        assertEquals(3L, profile.id());
        assertEquals("client@test.com", profile.email());
        assertEquals("CLIENT", profile.role());
        assertEquals("ACTIVE", profile.status());
    }
}
