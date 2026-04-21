package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTrainerAssignmentRequest {

    @NotNull(message = ApplicationConstants.TRAINER_ID_REQUIRED)
    private Long trainerId;

    @Size(max = ApplicationConstants.MESSAGE_MAX_LENGTH, message = ApplicationConstants.MESSAGE_MAX_SIZE)
    private String message;
}
 