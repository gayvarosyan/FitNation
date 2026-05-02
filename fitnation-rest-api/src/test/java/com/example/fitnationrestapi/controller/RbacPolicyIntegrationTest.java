package com.example.fitnationrestapi.controller;

import com.example.fitnationbooking.service.ClassBookingService;
import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.rbac.RbacPolicyService;
import com.example.fitnationrestapi.endpoint.UserBookingEndpoint;
import com.example.fitnationrestapi.exception.GlobalExceptionHandler;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RbacPolicyIntegrationTest {

    @Mock
    private ClassBookingService classBookingService;

    @Mock
    private GroupClassService groupClassService;

    private MockMvc mockMvc;

    private User clientUser;
    private User trainerUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserBookingEndpoint(classBookingService, groupClassService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        clientUser = buildUser(1L, UserRole.CLIENT);
        trainerUser = buildUser(2L, UserRole.TRAINER);
        adminUser = buildUser(3L, UserRole.ADMIN);

        when(classBookingService.getUserBookings(anyLong(), anyInt(), anyInt(), anyString(), any()))
                .thenReturn(emptyPagedResponse());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private PagedResponse<UserBookingItemResponse> emptyPagedResponse() {
        return PagedResponse.<UserBookingItemResponse>builder()
                .items(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .hasNext(false)
                .sort("createdAt,desc")
                .build();
    }

    @Test
    void getBookings_whenClient_returns200() throws Exception {
        authenticateAs(clientUser);
        mockMvc.perform(get("/api/users/bookings"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_whenTrainer_returns200() throws Exception {
        authenticateAs(trainerUser);
        mockMvc.perform(get("/api/users/bookings"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_whenAdmin_returns200() throws Exception {
        authenticateAs(adminUser);
        mockMvc.perform(get("/api/users/bookings"))
                .andExpect(status().isOk());
    }

    @Test
    void bookClass_whenClient_returns201() throws Exception {
        authenticateAs(clientUser);
        mockMvc.perform(post("/api/users/classes/1/book"))
                .andExpect(status().isCreated());
    }

    @Test
    void bookClass_whenTrainer_returns201() throws Exception {
        authenticateAs(trainerUser);
        mockMvc.perform(post("/api/users/classes/1/book"))
                .andExpect(status().isCreated());
    }


    @Test
    void cancelBooking_whenClient_returns204() throws Exception {
        authenticateAs(clientUser);
        mockMvc.perform(put("/api/users/bookings/1/cancel"))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelBooking_whenAdmin_returns204() throws Exception {
        authenticateAs(adminUser);
        mockMvc.perform(put("/api/users/bookings/1/cancel"))
                .andExpect(status().isNoContent());
    }
    @Test
    void requireAdmin_whenClient_throwsForbidden() {
        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> new RbacPolicyService().requireAdmin(UserRole.CLIENT)
        );
        assertTrue(ex.getMessage().startsWith("ROLE_NOT_ALLOWED"));
    }

    @Test
    void requireAdmin_whenTrainer_throwsForbidden() {
        assertThrows(
                ForbiddenOperationException.class,
                () -> new RbacPolicyService().requireAdmin(UserRole.TRAINER)
        );
    }
    private void authenticateAs(User user) {
        var auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private User buildUser(Long id, UserRole role) {
        return User.builder()
                .id(id)
                .email(role.name().toLowerCase() + "@test.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .phone("1234567890")
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }
}