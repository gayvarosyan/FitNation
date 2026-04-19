package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.RedeemQrRequest;
import com.example.fitnationcommon.dto.response.CreateQrSessionResponse;
import com.example.fitnationcommon.enums.DeviceSessionStatus;
import com.example.fitnationuser.device.service.DeviceLoginSessionService;
import com.example.fitnationuser.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Device Login Session", description = "APIs for QR-based device login session management")
public class DeviceLoginSessionEndpoint {

    private final DeviceLoginSessionService sessionService;

    @Operation(
            summary = "Create QR login session",
            description = "Generates a new QR session for the authenticated user to log in on another device.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "QR session created successfully",
                    content = @Content(schema = @Schema(implementation = CreateQrSessionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized – user is not authenticated",
                    content = @Content)
    })
    @PostMapping("/devices/qr-session")
    public ResponseEntity<CreateQrSessionResponse> createSession(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(sessionService.createSession(currentUser));
    }

    @Operation(
            summary = "Redeem QR session",
            description = "Exchanges a QR payload for an access token. No authentication required."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session redeemed – returns access token",
                    content = @Content(schema = @Schema(example = "{\"accessToken\": \"eyJ...\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired QR payload",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Session not found",
                    content = @Content)
    })
    @PostMapping("/login/qr")
    public ResponseEntity<Map<String, String>> redeemSession(
            @Valid @RequestBody RedeemQrRequest request) {
        String accessToken = sessionService.redeemSession(request.getQrPayload());
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @Operation(
            summary = "Get QR session status",
            description = "Returns the current status of a QR login session for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session status returned",
                    content = @Content(schema = @Schema(example = "{\"status\": \"PENDING\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized – user is not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Session not found",
                    content = @Content)
    })
    @GetMapping("/devices/qr-session/{sessionId}")
    public ResponseEntity<Map<String, String>> getSessionStatus(
            @Parameter(description = "The QR session ID to check", required = true)
            @PathVariable String sessionId,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        DeviceSessionStatus status = sessionService.getSessionStatus(sessionId, currentUser);
        return ResponseEntity.ok(Map.of("status", status.name()));
    }
}
