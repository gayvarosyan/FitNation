package com.example.fitnationweb.controller;

import com.example.fitnationweb.service.MvcMembershipRequestAdminService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/admin/subscription-requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubscriptionRequestMvcController {

    private static final String PAGE_PATH = "/admin/subscription-requests";

    private final MvcMembershipRequestAdminService mvcMembershipRequestAdminService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping
    public String list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        model.addAttribute("requests", mvcMembershipRequestAdminService.list(status, page, size));
        model.addAttribute("statusFilter", status != null ? status : "");
        model.addAttribute("navSection", "subscription-requests");
        return "admin/subscription-requests";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var actor = currentUserAccessor.requireUser();
        var result = mvcMembershipRequestAdminService.approve(PAGE_PATH, id, actor);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        var actor = currentUserAccessor.requireUser();
        var result = mvcMembershipRequestAdminService.reject(PAGE_PATH, id, actor, reason);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
