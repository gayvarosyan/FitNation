package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationprogress.dto.ProgressSummaryResponse;
import com.example.fitnationprogress.service.UserProgressService;
import com.example.fitnationrestapi.endpoint.TrainerClientProgressEndpoint;
import com.example.fitnationrestapi.exception.GlobalExceptionHandler;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerClientProgressEndpointApiTest {

    @Mock
    private UserProgressService userProgressService;
    @Mock
    private CurrentUserHelper currentUserHelper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new TrainerClientProgressEndpoint(userProgressService, currentUserHelper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void summary_returns200_forAssignedTrainer() throws Exception {
        when(currentUserHelper.getId()).thenReturn(9L);
        when(currentUserHelper.getRole()).thenReturn(UserRole.TRAINER);
        when(userProgressService.getSummaryByActor(any(), any(), any())).thenReturn(
                new ProgressSummaryResponse(null, null, null, null, 4L));

        mockMvc.perform(get("/api/trainers/clients/22/progress/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(4));
    }

    @Test
    void summary_returns403_forUnassignedTrainer() throws Exception {
        when(currentUserHelper.getId()).thenReturn(9L);
        when(currentUserHelper.getRole()).thenReturn(UserRole.TRAINER);
        when(userProgressService.getSummaryByActor(any(), any(), any()))
                .thenThrow(new ForbiddenOperationException("Trainer is not assigned to this client"));

        mockMvc.perform(get("/api/trainers/clients/22/progress/summary"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }
}
