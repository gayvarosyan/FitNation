package com.example.fitnationweb.endpoint;

import com.example.fitnationcommon.dto.request.RejectMembershipRequest;
import com.example.fitnationcommon.enums.MembershipRequestStatus;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
public class AdminSubscriptionRequestMvcEndpoint {

    private static final String PAGE_PATH = "/admin/subscription-requests";

    private final MembershipService membershipService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping
    public String list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        MembershipRequestStatus filter = parseStatus(status);
        var pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 100)));
        model.addAttribute("requests", membershipService.listMembershipRequestsForAdmin(filter, pageable));
        model.addAttribute("statusFilter", status != null ? status : "");
        model.addAttribute("navSection", "subscription-requests");
        return "admin/subscription-requests";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            membershipService.approveMembershipRequest(id, currentUserAccessor.requireUser());
            MvcRedirect.to(PAGE_PATH, "Request approved. Membership is now active.").applyTo(redirectAttributes);
        } catch (Exception e) {
            MvcRedirect.failure(PAGE_PATH, e.getMessage()).applyTo(redirectAttributes);
        }
        return "redirect:" + PAGE_PATH;
    }

    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        try {
            membershipService.rejectMembershipRequest(
                    id,
                    currentUserAccessor.requireUser(),
                    new RejectMembershipRequest(reason));
            MvcRedirect.to(PAGE_PATH, "Request rejected.").applyTo(redirectAttributes);
        } catch (Exception e) {
            MvcRedirect.failure(PAGE_PATH, e.getMessage()).applyTo(redirectAttributes);
        }
        return "redirect:" + PAGE_PATH;
    }

    private static MembershipRequestStatus parseStatus(String raw) {
        if (raw == null || raw.isBlank() || "ALL".equalsIgnoreCase(raw.trim())) {
            return null;
        }
        try {
            return MembershipRequestStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
