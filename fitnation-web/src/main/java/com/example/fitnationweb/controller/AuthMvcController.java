package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.request.LoginRequest;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationcommon.exception.InvalidPasswordException;
import com.example.fitnationcommon.exception.InvalidRoleException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationweb.service.AuthWebService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthMvcController {

    private final AuthWebService authWebService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginRequest") LoginRequest loginRequest) {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check your input.");
            return "redirect:/login";
        }
        try {
            return authWebService.loginAndCreateSession(request, loginRequest.email(), loginRequest.password());
        } catch (InvalidPasswordException | UserNotFoundException e) {
            log.warn("Failed login attempt for email={}: {}", loginRequest.email(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(
            @ModelAttribute("registerRequest") com.example.fitnationweb.controller.dto.RegisterRequest registerRequest) {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") com.example.fitnationweb.controller.dto.RegisterRequest registerRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", firstValidationError(bindingResult));
            return "redirect:/register";
        }
        try {
            authWebService.registerFromWebRequest(registerRequest);
            redirectAttributes.addFlashAttribute("message", "Registration successful. You can log in now.");
            return "redirect:/login";
        } catch (EmailAlreadyExistsException | InvalidRoleException e) {
            log.warn("Registration failed for email={}: {}", registerRequest.email(), e.getMessage());
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

    private static String firstValidationError(BindingResult bindingResult) {
        var error = bindingResult.getFieldError();
        if (error != null && error.getDefaultMessage() != null) {
            return error.getDefaultMessage();
        }
        return "Please check your input.";
    }
}
