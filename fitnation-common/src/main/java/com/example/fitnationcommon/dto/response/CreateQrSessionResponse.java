package com.example.fitnationcommon.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CreateQrSessionResponse {
    private String sessionId;
    private String qrPayload;
    private LocalDateTime expiresAt;
}
