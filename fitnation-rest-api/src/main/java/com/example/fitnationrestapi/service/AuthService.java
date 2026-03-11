package com.example.fitnationrestapi.service;

import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.dto.response.AuthResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.InvalidRoleException;
import com.example.fitnationtrainer.service.TrainerRegistrationService;
import com.example.fitnationuser.service.UserAuthService;
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

    public AuthResponse register(RegisterRequest request) {
        if (request.role() != UserRole.CLIENT && request.role() != UserRole.TRAINER) {
            throw new InvalidRoleException("Invalid role");
        }
        User user = request.role() == UserRole.CLIENT
                ? userRegistrationService.register(request)
                : trainerRegistrationService.register(request);
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                null,
                null
        );
    }

    public AuthResponse login(String email, String rawPassword) {
        User user = userAuthService.login(email, rawPassword);
        boolean isAdmin = user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPER_ADMIN;
        String redirectUrl = isAdmin ? "/admin-trainers.html" : null;
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                redirectUrl,
                token
        );
    }
}
