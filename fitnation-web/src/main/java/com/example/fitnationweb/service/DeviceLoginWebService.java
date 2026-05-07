package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.response.CreateQrSessionResponse;
import com.example.fitnationcommon.enums.DeviceSessionStatus;
import com.example.fitnationuser.device.service.DeviceLoginSessionService;
import com.example.fitnationuser.user.User;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
@RequiredArgsConstructor
public class DeviceLoginWebService {

    private static final String DEVICE_LOGIN_PATH = "/portal/device-login";
    private static final String LOGIN_PATH = "/login";

    private final DeviceLoginSessionService sessionService;

    public CreateQrSessionResponse createSession(User currentUser) {
        return sessionService.createSession(currentUser);
    }

    public String redeemSession(String qrPayload) {
        return sessionService.redeemSession(qrPayload);
    }

    public DeviceSessionStatus getSessionStatus(String sessionId, User currentUser) {
        return sessionService.getSessionStatus(sessionId, currentUser);
    }

    public void populateDeviceLoginPage(Model model, String sessionId, User currentUser) {
        model.addAttribute("navSection", "device-login");
        if (sessionId != null && !sessionId.isBlank()) {
            model.addAttribute("sessionStatus", getSessionStatus(sessionId, currentUser).name());
        }
    }

    public MvcRedirect createSessionRedirect(User currentUser, RedirectAttributes redirectAttributes) {
        try {
            var response = createSession(currentUser);
            redirectAttributes.addFlashAttribute("createdSession", response);
            return MvcRedirect.to(DEVICE_LOGIN_PATH + "?sessionId=" + response.getSessionId(), "QR session created.");
        } catch (Exception e) {
            return MvcRedirect.failure(DEVICE_LOGIN_PATH, e.getMessage());
        }
    }

    public MvcRedirect redeemRedirect(String qrPayload, RedirectAttributes redirectAttributes) {
        try {
            String token = redeemSession(qrPayload);
            redirectAttributes.addFlashAttribute("accessToken", token);
            return MvcRedirect.to(LOGIN_PATH, "QR redeemed successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(LOGIN_PATH, e.getMessage());
        }
    }
}
