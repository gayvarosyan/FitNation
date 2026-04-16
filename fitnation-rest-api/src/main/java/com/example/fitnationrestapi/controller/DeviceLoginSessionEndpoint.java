package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.RedeemQrRequest;
import com.example.fitnationcommon.dto.response.CreateQrSessionResponse;
import com.example.fitnationcommon.enums.DeviceSessionStatus;
import com.example.fitnationuser.device.DeviceLoginSessionService;
import com.example.fitnationuser.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class DeviceLoginSessionEndpoint {

    private final DeviceLoginSessionService sessionService;

    @PostMapping("/devices/qr-session")
    public ResponseEntity<CreateQrSessionResponse> createSession(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(sessionService.createSession(currentUser));
    }

    @PostMapping("/login/qr")
    public ResponseEntity<Map<String, String>> redeemSession(
            @Valid @RequestBody RedeemQrRequest request) {
        String accessToken = sessionService.redeemSession(request.getQrPayload());
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @GetMapping("/devices/qr-session/{sessionId}")
    public ResponseEntity<Map<String, String>> getSessionStatus(
            @PathVariable String sessionId,
            @AuthenticationPrincipal User currentUser) {
        DeviceSessionStatus status = sessionService.getSessionStatus(sessionId, currentUser);
        return ResponseEntity.ok(Map.of("status", status.name()));
    }
}
