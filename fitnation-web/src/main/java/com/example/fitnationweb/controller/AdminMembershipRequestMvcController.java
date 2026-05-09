package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.response.AdminMembershipRequestResponse;
import com.example.fitnationweb.service.MvcMembershipRequestAdminService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/membership-requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMembershipRequestMvcController {

    private static final String REQUESTS_PATH = "/admin/membership-requests";

    private final MvcMembershipRequestAdminService mvcMembershipRequestAdminService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping
    public String listMembershipRequests(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        try {
            Page<AdminMembershipRequestResponse> requests = mvcMembershipRequestAdminService.list(status, page, size);
            model.addAttribute("requests", requests);
            model.addAttribute("statusFilter", status != null ? status : "");
            model.addAttribute("navSection", "membership-requests");
            return "admin/membership-requests";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/membership-requests";
        }
    }

    @PostMapping("/{id}/approve")
    public String approveMembershipRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var actor = currentUserAccessor.requireUser();
        var result = mvcMembershipRequestAdminService.approve(REQUESTS_PATH, id, actor);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/{id}/reject")
    public String rejectMembershipRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        var actor = currentUserAccessor.requireUser();
        var result = mvcMembershipRequestAdminService.reject(REQUESTS_PATH, id, actor, reason);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
