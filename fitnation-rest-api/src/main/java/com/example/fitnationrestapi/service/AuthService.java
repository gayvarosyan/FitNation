package com.example.fitnationrestapi.service;

import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.dto.response.AuthResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.InvalidRoleException;
import com.example.fitnationtrainer.service.TrainerRegistrationService;
import com.example.fitnationuser.security.JwtService;
import com.example.fitnationuser.service.UserAuthService;
import com.example.fitnationuser.service.UserStatusUtil;
import com.example.fitnationuser.service.UserRegistrationService;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRegistrationService userRegistrationService;
    private final TrainerRegistrationService trainerRegistrationService;
    private final UserAuthService userAuthService;
    private final JwtService jwtService;
    private final UserStatusUtil userStatusUtil;

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getExpiration()
        );
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.role() != UserRole.CLIENT && request.role() != UserRole.TRAINER) {
            throw new InvalidRoleException("Invalid role");
        }
        User user = request.role() == UserRole.CLIENT
                ? userRegistrationService.register(request)
                : trainerRegistrationService.register(request);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse login(String email, String rawPassword) {
        User user = userAuthService.login(email, rawPassword);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String email = jwtService.extractEmail(refreshToken);
        User user = userAuthService.findByEmail(email);

        userStatusUtil.ensureActive(user);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }
}