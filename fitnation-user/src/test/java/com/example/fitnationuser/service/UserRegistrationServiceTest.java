package com.example.fitnationuser.service;

import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationuser.mapper.UserMapper;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "Jane", "Doe", "jane@example.com", "Secure1@x", "+12345678901",
                UserRole.CLIENT, null, null);
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class, () -> userRegistrationService.register(request));
    }

    @Test
    void register_savesAndReturnsMappedUser() {
        RegisterRequest request = new RegisterRequest(
                "Jane", "Doe", "jane@example.com", "Secure1@x", "+12345678901",
                UserRole.CLIENT, null, null);
        User mapped = User.builder().email("jane@example.com").build();
        User saved = User.builder().id(10L).email("jane@example.com").build();

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
        when(userMapper.toUser(request)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(saved);

        User result = userRegistrationService.register(request);

        assertSame(saved, result);
        verify(userRepository).save(mapped);
    }
}
