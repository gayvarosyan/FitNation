package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.response.AdminMembershipRecordResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationrestapi.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminMembershipApiIntegrationTest {

    @Mock
    private MembershipService membershipService;

    private MockMvc mockMvc;

    private void initMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MembershipController(membershipService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void adminMemberships_withoutBearerToken_returns200() throws Exception {
        initMvc();

        when(membershipService.getAdminMemberships(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/admin/memberships"))
                .andExpect(status().isOk());
    }

    @Test
    void adminMemberships_withValidJwt_returnsEmptyList() throws Exception {
        initMvc();

        AdminMembershipRecordResponse record = new AdminMembershipRecordResponse(
                1L, 2L, "Ann", "Client", "ann@test.com",
                3L, "Premium", 30, BigDecimal.valueOf(59.99),
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1),
                MembershipStatus.ACTIVE, null, null, null
        );

        when(membershipService.getAdminMemberships(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(record)));

        mockMvc.perform(get("/api/admin/memberships")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].userEmail").value("ann@test.com"))
                .andExpect(jsonPath("$.items[0].status").value("ACTIVE"));
    }
}