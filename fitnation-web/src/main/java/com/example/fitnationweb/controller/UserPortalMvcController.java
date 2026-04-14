package com.example.fitnationweb.controller;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.SubmitMembershipRequest;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/portal")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
public class UserPortalMvcController {

    private static final String SUBSCRIPTIONS_VIEW = "portal/subscriptions";
    private static final String SUBSCRIPTIONS_PATH = "/portal/subscriptions";

    private final MembershipService membershipService;
    private final NutritionPlanService nutritionPlanService;
    private final TrainerManagementService trainerManagementService;
    private final GroupClassService groupClassService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("navSection", "home");
        return "portal/home";
    }

    @GetMapping("/subscriptions")
    public String subscriptions(Model model) {
        String email = currentUserAccessor.requireUser().getEmail();
        model.addAttribute("navSection", "subscriptions");
        model.addAttribute("plans", membershipService.getAllMembershipTypes());
        model.addAttribute("myRequests", membershipService.getUserMembershipRequests(email));
        model.addAttribute("myMemberships", membershipService.getUserMemberships(email));
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("activeMembership", resolveActiveMembership(membershipService.getUserMemberships(email)));
        addBundleLabelMaps(model);
        return SUBSCRIPTIONS_VIEW;
    }

    @PostMapping("/subscriptions/request")
    public String requestSubscription(
            @RequestParam Long membershipTypeId,
            RedirectAttributes redirectAttributes) {
        try {
            membershipService.submitMembershipRequest(
                    currentUserAccessor.requireUser().getEmail(),
                    new SubmitMembershipRequest(membershipTypeId));
            MvcRedirect.to(SUBSCRIPTIONS_PATH, "Subscription request submitted.").applyTo(redirectAttributes);
        } catch (Exception e) {
            MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage()).applyTo(redirectAttributes);
        }
        return "redirect:" + SUBSCRIPTIONS_PATH;
    }

    private void addBundleLabelMaps(Model model) {
        Map<Long, String> nutritionNames = new HashMap<>();
        for (NutritionPlanCatalogItemDto n : nutritionPlanService.getPlanCatalog()) {
            nutritionNames.put(n.getId(), n.getPlanName());
        }
        Map<Long, String> trainerNames = new HashMap<>();
        for (TrainerDirectoryItem t : trainerManagementService.getDirectory()) {
            try {
                if (t.trainerId() != null && !t.trainerId().isBlank()) {
                    trainerNames.put(Long.parseLong(t.trainerId().trim()),
                            t.firstName() + " " + t.lastName());
                }
            } catch (NumberFormatException ignored) {
                // skip malformed directory id
            }
        }
        Map<Long, String> groupClassNames = new HashMap<>();
        for (var gc : groupClassService.listAllGroupClasses()) {
            groupClassNames.put(gc.id(), gc.name());
        }
        model.addAttribute("nutritionNames", nutritionNames);
        model.addAttribute("trainerNames", trainerNames);
        model.addAttribute("groupClassNames", groupClassNames);
    }

    private static MembershipResponse resolveActiveMembership(List<MembershipResponse> memberships) {
        LocalDate today = LocalDate.now();
        Optional<MembershipResponse> best = Optional.empty();
        for (MembershipResponse m : memberships) {
            if (m.status() != MembershipStatus.ACTIVE || m.endDate().isBefore(today)) {
                continue;
            }
            if (best.isEmpty() || m.endDate().isAfter(best.get().endDate())) {
                best = Optional.of(m);
            }
        }
        return best.orElse(null);
    }
}
