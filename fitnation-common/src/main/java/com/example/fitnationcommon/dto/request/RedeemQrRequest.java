package com.example.fitnationcommon.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedeemQrRequest {
    @NotBlank
    private String qrPayload;
}
