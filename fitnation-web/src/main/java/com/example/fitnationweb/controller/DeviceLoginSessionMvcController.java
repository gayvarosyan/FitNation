package com.example.fitnationweb.controller;

import com.example.fitnationweb.service.DeviceLoginWebService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class DeviceLoginSessionMvcController {

    private final DeviceLoginWebService deviceLoginWebService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping("/portal/device-login")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String page(@RequestParam(required = false) String sessionId, Model model) {
        var user = currentUserAccessor.requireUser();
        deviceLoginWebService.populateDeviceLoginPage(model, sessionId, user);
        return "portal/device-login";
    }

    @PostMapping("/portal/device-login/create")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String createSession(RedirectAttributes redirectAttributes) {
        var result = deviceLoginWebService.createSessionRedirect(currentUserAccessor.requireUser(), redirectAttributes);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/login/qr")
    public String redeem(@RequestParam String qrPayload, RedirectAttributes redirectAttributes) {
        var result = deviceLoginWebService.redeemRedirect(qrPayload, redirectAttributes);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
