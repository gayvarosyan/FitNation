package com.example.fitnationcommon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveRejectTrainerRequestRequest {

    @NotNull(message = "requestId is required")
    private Long requestId;
}
