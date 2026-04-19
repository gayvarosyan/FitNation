package com.example.fitnationcommon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateQrSessionResponse {
    private String sessionId;
    private String qrPayload;
    private LocalDateTime expiresAt;
}