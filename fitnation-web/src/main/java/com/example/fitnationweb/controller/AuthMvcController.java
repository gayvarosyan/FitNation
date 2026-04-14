package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.request.LoginRequest;
import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.InvalidPasswordException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationuser.security.JwtSessionConstants;
import com.example.fitnationweb.service.AuthWebService;
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

    private final AuthWebService authWebService;

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
            var auth = authWebService.login(loginRequest.email(), loginRequest.password());
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
            authWebService.register(new RegisterRequest(
                    firstName.trim(),
                    lastName.trim(),
                    email.trim(),
                    password,
                    phone.trim(),
                    role,
                    specialization != null ? specialization.trim() : null,
                    bio != null ? bio.trim() : null
            ));
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

    private static String redirectPathAfterLogin(String role) {
        return switch (role) {
            case "ADMIN" -> "redirect:/admin/trainers";
            case "CLIENT", "TRAINER" -> "redirect:/portal";
            default -> "redirect:/portal";
        };
    }
}
