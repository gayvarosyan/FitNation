package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
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

    @NotNull(message = ApplicationConstants.REQUEST_ID_REQUIRED)
    private Long requestId;
}
