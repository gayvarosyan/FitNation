package com.example.fitnationweb.controller;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.UpdateMembershipRequest;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import com.example.fitnationweb.support.MvcRedirect;
import com.example.fitnationmembership.service.MembershipService;
import com.fitnationnutrition.service.NutritionPlanService;
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
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class MembershipMvcController {

    private static final String SUBSCRIPTIONS_PATH = "/admin/subscriptions";

    private final MembershipService membershipService;
    private final NutritionPlanService nutritionPlanService;
    private final TrainerManagementService trainerManagementService;
    private final GroupClassService groupClassService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("membershipTypes", membershipService.getAllMembershipTypes());
        model.addAttribute("records", membershipService.getAdminMemberships());
        model.addAttribute("stats", membershipService.getAdminMembershipStats());
        model.addAttribute("nutritionPlans", nutritionPlanService.getPlanCatalog());
        model.addAttribute("trainerOptions", trainerManagementService.getDirectory());
        model.addAttribute("groupClasses", groupClassService.listAllGroupClasses());
        model.addAttribute("navSection", "subscriptions");
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
        var result = saveMembershipType(planId, name, durationDays, price, description, nutritionPlanId, trainerId, groupClassId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/plans/{id}/delete")
    public String deletePlan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = deleteMembershipType(id);
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
        var result = updateMembershipRecord(
                membershipId, membershipTypeId, startDate, endDate, status,
                nutritionPlanId, trainerId, groupClassId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/memberships/cancel")
    public String cancelMembership(@RequestParam Long membershipId, RedirectAttributes redirectAttributes) {
        var result = cancelMembershipInternal(membershipId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    private MvcRedirect saveMembershipType(
            Long planId,
            String name,
            int durationDays,
            BigDecimal price,
            String description,
            String nutritionPlanIdRaw,
            String trainerIdRaw,
            String groupClassIdRaw) {
        try {
            var body = new CreateMembershipTypeRequest(
                    name,
                    durationDays,
                    price,
                    description,
                    parseLongOrNull(nutritionPlanIdRaw),
                    parseLongOrNull(trainerIdRaw),
                    parseLongOrNull(groupClassIdRaw)
            );
            if (planId == null) {
                membershipService.createMembershipType(body);
                return MvcRedirect.to(SUBSCRIPTIONS_PATH, "Membership plan created.");
            }
            membershipService.updateMembershipType(planId, body);
            return MvcRedirect.to(SUBSCRIPTIONS_PATH, "Plan updated.");
        } catch (Exception e) {
            return MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage());
        }
    }

    private MvcRedirect deleteMembershipType(Long id) {
        try {
            membershipService.deleteMembershipType(id);
            return MvcRedirect.to(SUBSCRIPTIONS_PATH, "Plan deleted.");
        } catch (Exception e) {
            return MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage());
        }
    }

    private MvcRedirect updateMembershipRecord(
            Long membershipId,
            Long membershipTypeId,
            LocalDate startDate,
            LocalDate endDate,
            MembershipStatus status,
            String nutritionPlanIdRaw,
            String trainerIdRaw,
            String groupClassIdRaw) {
        try {
            membershipService.updateMembership(
                    membershipId,
                    new UpdateMembershipRequest(
                            membershipTypeId,
                            startDate,
                            endDate,
                            status,
                            parseLongOrNull(nutritionPlanIdRaw),
                            parseLongOrNull(trainerIdRaw),
                            parseLongOrNull(groupClassIdRaw)
                    ),
                    currentUserAccessor.requireUser()
            );
            return MvcRedirect.to(SUBSCRIPTIONS_PATH, "Membership updated.");
        } catch (Exception e) {
            return MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage());
        }
    }

    private MvcRedirect cancelMembershipInternal(Long membershipId) {
        try {
            membershipService.cancelMembership(membershipId, currentUserAccessor.requireUser());
            return MvcRedirect.to(SUBSCRIPTIONS_PATH, "Subscription cancelled.");
        } catch (Exception e) {
            return MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage());
        }
    }

    private static Long parseLongOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Long.parseLong(raw.trim());
    }
}
