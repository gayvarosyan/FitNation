package com.example.fitnationcommon.dto.request;

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

    @NotNull(message = "trainerId is required")
    private Long trainerId;

    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;
}
 