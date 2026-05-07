package com.example.fitnationweb.service;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.UpdateMembershipRequest;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationuser.user.User;
import com.example.fitnationweb.support.MvcRedirect;
import com.fitnationnutrition.service.NutritionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MvcMembershipAdminService {

    private static final String SUBSCRIPTIONS_PATH = "/admin/subscriptions";

    private final MembershipService membershipService;
    private final NutritionPlanService nutritionPlanService;
    private final TrainerManagementService trainerManagementService;
    private final GroupClassService groupClassService;

    public void populatePageModel(Model model) {
        model.addAttribute("membershipTypes", membershipService.getAllMembershipTypes());
        model.addAttribute("records", membershipService.getAdminMemberships(
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "startDate")), null, null).getContent());
        model.addAttribute("stats", membershipService.getAdminMembershipStats());
        model.addAttribute("nutritionPlans", nutritionPlanService.getPlanCatalog());
        model.addAttribute("trainerOptions", trainerManagementService.getDirectory(0, 100, null, null, null).getItems());
        model.addAttribute("groupClasses", groupClassService.listAllGroupClasses());
        model.addAttribute("navSection", "subscriptions");
    }

    public MvcRedirect savePlan(
            Long planId,
            String name,
            int durationDays,
            BigDecimal price,
            String description,
            String nutritionPlanId,
            String trainerId,
            String groupClassId) {
        try {
            var body = toMembershipTypeRequest(name, durationDays, price, description, nutritionPlanId, trainerId, groupClassId);
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

    public MvcRedirect deletePlan(Long id) {
        try {
            membershipService.deleteMembershipType(id);
            return MvcRedirect.to(SUBSCRIPTIONS_PATH, "Plan deleted.");
        } catch (Exception e) {
            return MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage());
        }
    }

    public MvcRedirect saveMembership(
            User currentUser,
            Long membershipId,
            Long membershipTypeId,
            LocalDate startDate,
            LocalDate endDate,
            MembershipStatus status,
            String nutritionPlanId,
            String trainerId,
            String groupClassId) {
        try {
            membershipService.updateMembership(
                    membershipId,
                    toUpdateMembershipRequest(
                            membershipTypeId, startDate, endDate, status, nutritionPlanId, trainerId, groupClassId),
                    currentUser
            );
            return MvcRedirect.to(SUBSCRIPTIONS_PATH, "Membership updated.");
        } catch (Exception e) {
            return MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage());
        }
    }

    public MvcRedirect cancelMembership(User currentUser, Long membershipId) {
        try {
            membershipService.cancelMembership(membershipId, currentUser);
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

    private static CreateMembershipTypeRequest toMembershipTypeRequest(
            String name,
            int durationDays,
            BigDecimal price,
            String description,
            String nutritionPlanId,
            String trainerId,
            String groupClassId) {
        return new CreateMembershipTypeRequest(
                name,
                durationDays,
                price,
                description,
                parseLongOrNull(nutritionPlanId),
                parseLongOrNull(trainerId),
                parseLongOrNull(groupClassId)
        );
    }

    private static UpdateMembershipRequest toUpdateMembershipRequest(
            Long membershipTypeId,
            LocalDate startDate,
            LocalDate endDate,
            MembershipStatus status,
            String nutritionPlanId,
            String trainerId,
            String groupClassId) {
        return new UpdateMembershipRequest(
                membershipTypeId,
                startDate,
                endDate,
                status,
                parseLongOrNull(nutritionPlanId),
                parseLongOrNull(trainerId),
                parseLongOrNull(groupClassId)
        );
    }
}

