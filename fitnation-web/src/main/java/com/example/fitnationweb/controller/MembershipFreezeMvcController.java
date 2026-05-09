package com.example.fitnationweb.controller;

import com.example.fitnationcommon.enums.FreezeRequestStatus;
import com.example.fitnationweb.service.MembershipFreezeWebService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class MembershipFreezeMvcController {

    private final MembershipFreezeWebService membershipFreezeWebService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping("/portal/memberships/{membershipId}/freeze")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String userFreezePage(@PathVariable Long membershipId, Model model) {
        var user = currentUserAccessor.requireUser();
        model.addAttribute("navSection", "subscriptions");
        model.addAttribute("membershipId", membershipId);
        model.addAttribute("requests", membershipFreezeWebService.getUserFreezeRequests(user, membershipId));
        return "portal/freeze";
    }

    @PostMapping("/portal/memberships/{membershipId}/freeze/submit")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String submit(
            @PathVariable Long membershipId,
            @RequestParam LocalDate freezeStart,
            @RequestParam LocalDate freezeEnd,
            RedirectAttributes redirectAttributes) {
        var result = membershipFreezeWebService.submitFreezeRequestForMvc(
                currentUserAccessor.requireUser(), membershipId, freezeStart, freezeEnd);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/portal/memberships/{membershipId}/renew")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String renew(
            @PathVariable Long membershipId,
            @RequestParam(required = false) Long nutritionPlanId,
            @RequestParam(required = false) Long trainerId,
            @RequestParam(required = false) Long groupClassId,
            RedirectAttributes redirectAttributes) {
        var result = membershipFreezeWebService.renewMembershipForMvc(
                currentUserAccessor.requireUser(), membershipId, nutritionPlanId, trainerId, groupClassId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @GetMapping("/admin/membership-freeze-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminList(@RequestParam(required = false) FreezeRequestStatus status, Model model) {
        model.addAttribute("navSection", "freeze-requests");
        model.addAttribute("requests", membershipFreezeWebService.listFreezeRequests(status));
        model.addAttribute("statusFilter", status);
        return "admin/freeze-requests";
    }

    @PostMapping("/admin/membership-freeze-requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approve(@PathVariable Long requestId, RedirectAttributes redirectAttributes) {
        var result = membershipFreezeWebService.approveFreezeRequestForMvc(requestId, currentUserAccessor.requireUser());
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/admin/membership-freeze-requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String reject(
            @PathVariable Long requestId,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        var result = membershipFreezeWebService.rejectFreezeRequestForMvc(
                requestId, currentUserAccessor.requireUser(), reason);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
