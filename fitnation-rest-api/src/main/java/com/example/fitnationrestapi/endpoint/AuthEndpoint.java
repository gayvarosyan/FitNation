package com.example.fitnationrestapi.endpoint;

import com.example.fitnationcommon.dto.request.LoginRequest;
import com.example.fitnationcommon.dto.request.RefreshTokenRequest;
import com.example.fitnationcommon.dto.response.AuthResponse;
import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationrestapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Public JWT login, registration, and token refresh")
public class AuthEndpoint {

    private final AuthService authService;

    @Operation(summary = "Register", description = "Creates a user account and returns access and refresh tokens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or email already in use")
    })
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Login", description = "Authenticates credentials and returns access and refresh tokens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
    @Operation(summary = "Refresh access token", description = "Issues a new access token using a valid refresh token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }
}