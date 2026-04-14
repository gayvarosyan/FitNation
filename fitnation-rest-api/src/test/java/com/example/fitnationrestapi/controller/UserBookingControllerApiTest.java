package com.example.fitnationrestapi.controller;

import com.example.fitnationbooking.service.ClassBookingService;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationrestapi.exception.GlobalExceptionHandler;
import com.example.fitnationuser.security.SecurityAuthoritiesUtil;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserBookingControllerApiTest {

    @Mock
    private ClassBookingService classBookingService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserBookingController(classBookingService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void bookClass_returns201_andDelegatesWithCurrentUser() throws Exception {
        setAuthenticatedUser(42L);

        mockMvc.perform(post("/api/users/classes/7/book"))
                .andExpect(status().isCreated());

        verify(classBookingService).bookClass(7L, 42L);
    }

    @Test
    void getUserBookings_returns200_withPayload() throws Exception {
        setAuthenticatedUser(42L);
        when(classBookingService.getUserBookings(42L)).thenReturn(List.of(
                new UserBookingItemResponse(
                        10L, "Yoga Flow", "Ann Trainer",
                        LocalDate.of(2026, 4, 20),
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0),
                        ClassBookingStatus.BOOKED
                )));

        mockMvc.perform(get("/api/users/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(10))
                .andExpect(jsonPath("$[0].className").value("Yoga Flow"))
                .andExpect(jsonPath("$[0].status").value("BOOKED"));
    }

    private void setAuthenticatedUser(Long userId) {
        User principal = User.builder()
                .id(userId)
                .email("client@test.com")
                .firstName("Client")
                .lastName("User")
                .phone("+15550001111")
                .password("unused")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();
        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                SecurityAuthoritiesUtil.authoritiesForRole(UserRole.CLIENT));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
