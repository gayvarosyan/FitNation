package com.example.fitnationweb.controller;

import com.example.fitnationweb.service.UserProfileWebService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserProfileMvcController {

    private final UserProfileWebService userProfileWebService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping("/portal/profile")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String profile(Model model) {
        var user = currentUserAccessor.requireUser();
        model.addAttribute("navSection", "profile");
        model.addAttribute("profile", userProfileWebService.getMyProfile(user.getEmail()));
        return "portal/profile";
    }
}
