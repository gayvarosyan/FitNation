package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.TrainerAssignmentRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerAssignmentRequestResponse {

    private Long id;

    private Long clientUserId;
    private String clientFullName;

    private Long trainerUserId;
    private String trainerFullName;

    private TrainerAssignmentRequestStatus status;
    private String message;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}