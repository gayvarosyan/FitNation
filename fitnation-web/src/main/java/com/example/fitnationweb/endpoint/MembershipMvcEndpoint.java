package com.example.fitnationweb.endpoint;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.UpdateMembershipRequest;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import com.example.fitnationweb.support.MvcRedirect;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MembershipMvcEndpoint {

    private static final String SUBSCRIPTIONS_PATH = "/admin/subscriptions";

    private final MembershipService membershipService;
    private final NutritionPlanService nutritionPlanService;
    private final TrainerManagementService trainerManagementService;
    private final GroupClassService groupClassService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("membershipTypes", membershipService.getAllMembershipTypes());
        model.addAttribute("records", membershipService.getAdminMemberships(
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "startDate")), null, null).getContent());
        model.addAttribute("stats", membershipService.getAdminMembershipStats());
        model.addAttribute("nutritionPlans", nutritionPlanService.getPlanCatalog());
        model.addAttribute("trainerOptions", trainerManagementService.getDirectory(0, 100, null, null, null).getItems());
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
        try {
            var body = new CreateMembershipTypeRequest(
                    name,
                    durationDays,
                    price,
                    description,
                    parseLongOrNull(nutritionPlanId),
                    parseLongOrNull(trainerId),
                    parseLongOrNull(groupClassId)
            );
            if (planId == null) {
                membershipService.createMembershipType(body);
                MvcRedirect.to(SUBSCRIPTIONS_PATH, "Membership plan created.").applyTo(redirectAttributes);
            } else {
                membershipService.updateMembershipType(planId, body);
                MvcRedirect.to(SUBSCRIPTIONS_PATH, "Plan updated.").applyTo(redirectAttributes);
            }
        } catch (Exception e) {
            MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage()).applyTo(redirectAttributes);
        }
        return "redirect:" + SUBSCRIPTIONS_PATH;
    }

    @PostMapping("/plans/{id}/delete")
    public String deletePlan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            membershipService.deleteMembershipType(id);
            MvcRedirect.to(SUBSCRIPTIONS_PATH, "Plan deleted.").applyTo(redirectAttributes);
        } catch (Exception e) {
            MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage()).applyTo(redirectAttributes);
        }
        return "redirect:" + SUBSCRIPTIONS_PATH;
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
        try {
            membershipService.updateMembership(
                    membershipId,
                    new UpdateMembershipRequest(
                            membershipTypeId,
                            startDate,
                            endDate,
                            status,
                            parseLongOrNull(nutritionPlanId),
                            parseLongOrNull(trainerId),
                            parseLongOrNull(groupClassId)
                    ),
                    currentUserAccessor.requireUser()
            );
            MvcRedirect.to(SUBSCRIPTIONS_PATH, "Membership updated.").applyTo(redirectAttributes);
        } catch (Exception e) {
            MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage()).applyTo(redirectAttributes);
        }
        return "redirect:" + SUBSCRIPTIONS_PATH;
    }

    @PostMapping("/memberships/cancel")
    public String cancelMembership(@RequestParam Long membershipId, RedirectAttributes redirectAttributes) {
        try {
            membershipService.cancelMembership(membershipId, currentUserAccessor.requireUser());
            MvcRedirect.to(SUBSCRIPTIONS_PATH, "Subscription cancelled.").applyTo(redirectAttributes);
        } catch (Exception e) {
            MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage()).applyTo(redirectAttributes);
        }
        return "redirect:" + SUBSCRIPTIONS_PATH;
    }

    private static Long parseLongOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Long.parseLong(raw.trim());
    }
}
