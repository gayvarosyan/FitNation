package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.request.LoginRequest;
import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.dto.response.AuthResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.InvalidPasswordException;
import com.example.fitnationcommon.exception.InvalidRoleException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationtrainer.service.TrainerRegistrationService;
import com.example.fitnationuser.security.JwtService;
import com.example.fitnationuser.security.JwtSessionConstants;
import com.example.fitnationuser.service.UserAuthService;
import com.example.fitnationuser.service.UserRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthMvcController {

    private final UserRegistrationService userRegistrationService;
    private final TrainerRegistrationService trainerRegistrationService;
    private final UserAuthService userAuthService;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginRequest") LoginRequest loginRequest) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                        BindingResult bindingResult,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check your input.");
            return "redirect:/login";
        }
        try {
            var auth = loginAndBuildResponse(loginRequest.email(), loginRequest.password());
            HttpSession session = request.getSession(true);
            session.setAttribute(JwtSessionConstants.ACCESS_TOKEN, auth.accessToken());
            return redirectPathAfterLogin(auth.role());
        } catch (InvalidPasswordException | UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam UserRole role,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String bio,
            RedirectAttributes redirectAttributes) {
        try {
            register(buildRegisterRequest(firstName, lastName, email, password, phone, role, specialization, bio));
            redirectAttributes.addFlashAttribute("message", "Registration successful. You can log in now.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    private AuthResponse loginAndBuildResponse(String email, String rawPassword) {
        var user = userAuthService.login(email, rawPassword);
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

    private void register(RegisterRequest request) {
        if (request.role() != UserRole.CLIENT && request.role() != UserRole.TRAINER) {
            throw new InvalidRoleException("Invalid role");
        }
        if (request.role() == UserRole.CLIENT) {
            userRegistrationService.register(request);
            return;
        }
        trainerRegistrationService.register(request);
    }

    private static String redirectPathAfterLogin(String role) {
        if ("ADMIN".equals(role) || "SUPER_ADMIN".equals(role)) {
            return "redirect:/admin/trainers";
        }
        return "redirect:/";
    }

    private static RegisterRequest buildRegisterRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            String phone,
            UserRole role,
            String specialization,
            String bio) {
        return new RegisterRequest(
                firstName.trim(),
                lastName.trim(),
                email.trim(),
                password,
                phone.trim(),
                role,
                specialization != null ? specialization.trim() : null,
                bio != null ? bio.trim() : null
        );
    }
}
