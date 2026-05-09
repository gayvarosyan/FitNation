package com.example.fitnationweb.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.dto.response.AuthResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.InvalidRoleException;
import com.example.fitnationtrainer.service.TrainerRegistrationService;
import com.example.fitnationuser.security.JwtSessionConstants;
import com.example.fitnationuser.security.JwtService;
import com.example.fitnationuser.service.UserAuthService;
import com.example.fitnationuser.service.UserRegistrationService;
import com.example.fitnationuser.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    public String loginAndCreateSession(HttpServletRequest request, String email, String rawPassword) {
        var auth = login(email, rawPassword);
        invalidateExistingSession(request);
        HttpSession session = request.getSession(true);
        session.setAttribute(JwtSessionConstants.ACCESS_TOKEN, auth.accessToken());
        return resolvePostLoginRedirect(auth.role());
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

    public void registerFromWebRequest(com.example.fitnationweb.controller.dto.RegisterRequest request) {
        register(new RegisterRequest(
                trimOrNull(request.firstName()),
                trimOrNull(request.lastName()),
                trimOrNull(request.email()),
                request.password(),
                trimOrNull(request.phone()),
                request.role(),
                trimOrNull(request.specialization()),
                trimOrNull(request.bio())
        ));
    }

    private static void invalidateExistingSession(HttpServletRequest request) {
        HttpSession existing = request.getSession(false);
        if (existing != null) {
            existing.invalidate();
        }
    }

    private static String resolvePostLoginRedirect(String role) {
        if (role == null) {
            return "redirect:/portal";
        }
        return switch (role) {
            case "ADMIN" -> "redirect:/admin/trainers";
            case "CLIENT", "TRAINER" -> "redirect:/portal";
            default -> "redirect:/portal";
        };
    }

    private static String trimOrNull(String value) {
        return value == null ? null : value.trim();
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
