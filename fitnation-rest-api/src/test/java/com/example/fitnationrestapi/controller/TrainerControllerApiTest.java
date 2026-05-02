package com.example.fitnationrestapi.controller;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.dto.response.TrainerStatsResponse;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
import com.example.fitnationrestapi.endpoint.TrainerEndpoint;
import com.example.fitnationrestapi.exception.GlobalExceptionHandler;
import com.example.fitnationtrainer.service.TrainerManagementService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerApiTest {

    @Mock
    private GroupClassService groupClassService;
    @Mock
    private TrainerManagementService trainerManagementService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new TrainerEndpoint(groupClassService, trainerManagementService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void getStats_returns200_withServicePayload() throws Exception {
        when(trainerManagementService.getStats()).thenReturn(new TrainerStatsResponse(5L, 3L));

        mockMvc.perform(get("/api/trainers/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTrainers").value(5))
                .andExpect(jsonPath("$.currentlyActive").value(3));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        String invalidBody = """
                {
                  "firstName": "",
                  "lastName": "Trainer",
                  "email": "invalid-email",
                  "password": "short",
                  "phone": "123",
                  "specialization": "Yoga",
                  "bio": "Bio"
                }
                """;

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    void edit_returns404_whenTrainerIsMissing() throws Exception {
        EditTrainerRequest editRequest = new EditTrainerRequest(
                "Ann", "Trainer", null, "+15550001111", "Yoga", "Bio", null);
        when(trainerManagementService.edit(any(), any()))
                .thenThrow(new TrainerNotFoundException("Trainer not found"));

        mockMvc.perform(put("/api/trainers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
