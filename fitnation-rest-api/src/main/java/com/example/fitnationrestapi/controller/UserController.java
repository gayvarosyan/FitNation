package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.response.UserProfileResponse;
import com.example.fitnationuser.service.UserProfileService;
import com.example.fitnationuser.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Current user profile")
public class UserController {

    private final UserProfileService userProfileService;

    @Operation(summary = "Get my profile", description = "Profile for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public UserProfileResponse getMyProfile(@AuthenticationPrincipal User user) {
        return userProfileService.getProfile(user.getEmail());
    }
}
