package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.TrainerAssignmentRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerPublicProfileResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String specialization;
    private String bio;

    private boolean canRequest;
    private TrainerAssignmentRequestStatus existingRequestStatus;
}
