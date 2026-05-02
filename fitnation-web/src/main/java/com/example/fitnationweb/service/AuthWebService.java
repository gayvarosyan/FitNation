package com.example.fitnationweb.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.dto.response.AuthResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.InvalidRoleException;
import com.example.fitnationtrainer.service.TrainerRegistrationService;
import com.example.fitnationuser.security.JwtService;
import com.example.fitnationuser.service.UserAuthService;
import com.example.fitnationuser.service.UserRegistrationService;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthWebService {

    private final UserRegistrationService userRegistrationService;
    private final TrainerRegistrationService trainerRegistrationService;
    private final UserAuthService userAuthService;
    private final JwtService jwtService;

    public AuthResponse login(String email, String rawPassword) {
        User user = userAuthService.login(email, rawPassword);
        return buildAuthResponse(user);
    }

    public void register(RegisterRequest request) {
        if (request.role() != UserRole.CLIENT && request.role() != UserRole.TRAINER) {
            throw new InvalidRoleException(ApplicationConstants.INVALID_ROLE);
        }
        switch (request.role()) {
            case CLIENT -> userRegistrationService.register(request);
            case TRAINER -> trainerRegistrationService.register(request);
            case ADMIN -> throw new InvalidRoleException(ApplicationConstants.INVALID_ROLE);
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
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
}
