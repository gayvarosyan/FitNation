package com.example.fitnationweb.controller;

import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationweb.service.MvcMembershipAdminService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import com.example.fitnationweb.support.MvcRedirect;
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
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MembershipMvcController {


    private final MvcMembershipAdminService mvcMembershipAdminService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping
    public String page(Model model) {
        mvcMembershipAdminService.populatePageModel(model);
        return "admin/subscriptions";
    }

    @PostMapping("/plans/save")
    public String savePlan(
            @RequestParam(required = false) Long planId,
            @RequestParam String name,
            @RequestParam int durationDays,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String nutritionPlanId,
            @RequestParam(required = false) String trainerId,
            @RequestParam(required = false) String groupClassId,
            RedirectAttributes redirectAttributes) {
        var result = mvcMembershipAdminService.savePlan(
                planId, name, durationDays, price, description, nutritionPlanId, trainerId, groupClassId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/plans/{id}/delete")
    public String deletePlan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = mvcMembershipAdminService.deletePlan(id);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/memberships/save")
    public String saveMembership(
            @RequestParam Long membershipId,
            @RequestParam Long membershipTypeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam MembershipStatus status,
            @RequestParam(required = false) String nutritionPlanId,
            @RequestParam(required = false) String trainerId,
            @RequestParam(required = false) String groupClassId,
            RedirectAttributes redirectAttributes) {
        var actor = currentUserAccessor.requireUser();
        var result = mvcMembershipAdminService.saveMembership(
                actor,
                membershipId,
                membershipTypeId,
                startDate,
                endDate,
                status,
                nutritionPlanId,
                trainerId,
                groupClassId
        );
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/memberships/cancel")
    public String cancelMembership(@RequestParam Long membershipId, RedirectAttributes redirectAttributes) {
        var actor = currentUserAccessor.requireUser();
        var result = mvcMembershipAdminService.cancelMembership(actor, membershipId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
