package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.BookingDisplayStatus;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationrestapi.endpoint.UserBookingEndpoint;
import com.example.fitnationrestapi.exception.GlobalExceptionHandler;
import com.example.fitnationrestapi.service.UserBookingFacadeService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserBookingControllerApiTest {

    @Mock
    private UserBookingFacadeService bookingFacadeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserBookingController(classBookingService, groupClassService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(
                        new org.springframework.security.web.method.annotation
                                .AuthenticationPrincipalArgumentResolver()
                )
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private UsernamePasswordAuthenticationToken auth(Long id) {
        User user = User.builder()
                .id(id)
                .email("client@test.com")
                .firstName("Client")
                .lastName("User")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                SecurityAuthoritiesUtil.authoritiesForRole(UserRole.CLIENT)
        );
    }

    @Test
    void bookClass_returns201_andDelegatesWithCurrentUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(auth(42L));

        mockMvc.perform(post("/api/users/classes/7/book"))
                .andExpect(status().isCreated());

        verify(bookingFacadeService).bookClass(7L);
    }

    @Test
    void getUserBookings_returns200_withPayload() throws Exception {
        List<UserBookingItemResponse> items = List.of(
                new UserBookingItemResponse(
                        10L,
                        "Yoga Flow",
                        "Ann Trainer",
                        LocalDate.of(2026, 4, 20),
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0),
                        ClassBookingStatus.BOOKED,
                        BookingDisplayStatus.UPCOMING
                )
        );

        PagedResponse<UserBookingItemResponse> pagedResponse =
                PagedResponse.<UserBookingItemResponse>builder()
                        .items(items)
                        .page(0)
                        .size(20)
                        .totalElements(1)
                        .totalPages(1)
                        .hasNext(false)
                        .sort("createdAt,desc")
                        .build();

        when(classBookingService.getUserBookings(anyLong(), anyInt(), anyInt(), anyString(), any()))
                .thenReturn(pagedResponse);

        SecurityContextHolder.getContext().setAuthentication(auth(42L));

        mockMvc.perform(get("/api/users/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].bookingId").value(10))
                .andExpect(jsonPath("$.items[0].className").value("Yoga Flow"))
                .andExpect(jsonPath("$.items[0].status").value("BOOKED"));
    }
}