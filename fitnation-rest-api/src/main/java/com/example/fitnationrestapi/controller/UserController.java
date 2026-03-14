package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.response.UserProfileResponse;
import com.example.fitnationuser.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.fitnationuser.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(@AuthenticationPrincipal User user) {
        return userProfileService.getProfile(user.getEmail());
    }
}
