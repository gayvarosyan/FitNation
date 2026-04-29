package com.example.fitnationrestapi.controller;

import com.example.fitnationprogress.dto.ProgressEntryResponse;
import com.example.fitnationprogress.dto.UpsertUserProgressEntryRequest;
import com.example.fitnationprogress.service.UserProgressService;
import com.example.fitnationrestapi.endpoint.UserProgressEndpoint;
import com.example.fitnationrestapi.exception.GlobalExceptionHandler;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserProgressEndpointApiTest {

    @Mock
    private UserProgressService userProgressService;
    @Mock
    private CurrentUserHelper currentUserHelper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserProgressEndpoint(userProgressService, currentUserHelper))
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void create_returns201() throws Exception {
        when(currentUserHelper.getId()).thenReturn(5L);
        when(userProgressService.createEntry(any(), any())).thenReturn(new ProgressEntryResponse(
                100L, 5L, LocalDateTime.of(2026, 4, 20, 9, 30),
                BigDecimal.valueOf(80), null, null, null, null, null,
                "note", LocalDateTime.now(), LocalDateTime.now()));

        var request = new UpsertUserProgressEntryRequest(
                LocalDateTime.of(2026, 4, 20, 9, 30),
                BigDecimal.valueOf(80),
                null,
                null,
                null,
                null,
                null,
                "note");

        mockMvc.perform(post("/api/users/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.userId").value(5));
    }

    @Test
    void create_returns400_onInvalidPayload() throws Exception {
        var invalidBody = """
                {
                  "weight": 80
                }
                """;

        mockMvc.perform(post("/api/users/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }
}
