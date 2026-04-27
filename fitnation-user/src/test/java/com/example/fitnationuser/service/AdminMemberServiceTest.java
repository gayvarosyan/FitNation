package com.example.fitnationuser.service;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.service.EmailService;
import com.example.fitnationcommon.validation.MemberValidator;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminMemberServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserAdminService userAdminService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberValidator memberValidator;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdminMemberService adminMemberService;

    @Test
    void inviteMember_savesPendingUser_andSendsInvitationEmail() {
        ReflectionTestUtils.setField(adminMemberService, "loginUrl", "http://localhost:8080/login");
        CreateMemberRequest request = new CreateMemberRequest();
        request.setFirstName("Ann");
        request.setLastName("Client");
        request.setEmail("ann@test.com");
        request.setPhone("+15550001111");
        request.setPassword("Secure1@x");

        when(userRepository.findByEmail("ann@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Secure1@x")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        var result = adminMemberService.inviteMember(request);

        ArgumentCaptor<User> savedCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedCaptor.capture());
        User saved = savedCaptor.getValue();
        assertEquals(UserRole.CLIENT, saved.getRole());
        assertEquals(UserStatus.PENDING, saved.getStatus());
        assertEquals("encoded-password", saved.getPassword());
        assertEquals("ann@test.com", result.getEmail());
        verify(emailService).sendInvitationEmail("ann@test.com", "Secure1@x", "http://localhost:8080/login");
    }

    @Test
    void updateMember_throwsWhenMemberIsPending() {
        User pendingMember = User.builder()
                .id(12L)
                .email("pending@test.com")
                .role(UserRole.CLIENT)
                .status(UserStatus.PENDING)
                .build();
        when(userRepository.findById(12L)).thenReturn(Optional.of(pendingMember));

        UpdateMemberRequest request = new UpdateMemberRequest();
        request.setFirstName("Updated");

        assertThrows(ForbiddenOperationException.class, () ->
                adminMemberService.updateMember(12L, request));
    }

    @Test
    void updateMember_throwsWhenEmailAlreadyTaken() {
        User member = User.builder()
                .id(3L)
                .email("old@test.com")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();
        when(userRepository.findById(3L)).thenReturn(Optional.of(member));
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.of(new User()));

        UpdateMemberRequest request = new UpdateMemberRequest();
        request.setEmail("new@test.com");

        assertThrows(EmailAlreadyExistsException.class, () ->
                adminMemberService.updateMember(3L, request));
    }

    @Test
    void getMembers_withSearchAndStatus_usesFilteredRepositoryCall() {
        User user = User.builder()
                .id(7L)
                .firstName("Jon")
                .lastName("Doe")
                .email("jon@test.com")
                .phone("555")
                .status(UserStatus.ACTIVE)
                .role(UserRole.CLIENT)
                .build();
        PageImpl<User> userPage = new PageImpl<>(List.of(user), PageRequest.of(0, 20), 1);
        when(userRepository.findActiveByRoleAndStatusAndSearch(eq(UserRole.CLIENT), eq(UserStatus.ACTIVE), eq("jon"), any()))
                .thenReturn(userPage);

        var page = adminMemberService.getMembers(0, 20, "jon", "active");
        
        assertNotNull(page, "Page should not be null");
        assertEquals(1, page.getTotalElements());
        assertEquals("jon@test.com", page.getContent().get(0).getEmail());
        verify(userRepository).findActiveByRoleAndStatusAndSearch(eq(UserRole.CLIENT), eq(UserStatus.ACTIVE), eq("jon"), any());
    }
}
