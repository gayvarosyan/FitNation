package com.example.fitnationweb.controller;

import com.example.fitnationweb.service.UserProfileWebService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserMvcController {

    private final UserProfileWebService userProfileWebService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping("/me")
    public String getMyProfile(Model model) {
        userProfileWebService.populateMyProfileModel(model, currentUserAccessor.requireUser().getEmail());
        return "user/profile";
    }
}
