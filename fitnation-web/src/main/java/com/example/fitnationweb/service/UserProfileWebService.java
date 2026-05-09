package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.response.UserProfileResponse;
import com.example.fitnationuser.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class UserProfileWebService {

    private final UserProfileService userProfileService;

    public UserProfileResponse getMyProfile(String email) {
        return userProfileService.getProfile(email);
    }

    public void populateMyProfileModel(Model model, String email) {
        try {
            model.addAttribute("profile", getMyProfile(email));
            model.addAttribute("navSection", "profile");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
    }
}
