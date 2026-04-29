package com.example.fitnationweb.endpoint;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.SubmitMembershipRequest;
import com.example.fitnationcommon.dto.response.GroupClassResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import com.example.fitnationweb.support.MvcRedirect;
import com.fitnationnutrition.service.NutritionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/portal")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
public class UserPortalMvcEndpoint {

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
        PageRequest pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "startDate"));

        List<MembershipResponse> myMemberships = membershipService
                .getUserMemberships(email, pageable)
                .getContent();

        model.addAttribute("navSection", "subscriptions");
        model.addAttribute("plans", membershipService.getAllMembershipTypes());
        model.addAttribute("myRequests", membershipService.getUserMembershipRequests(email));
        model.addAttribute("myMemberships", myMemberships);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("activeMembership", resolveActiveMembership(myMemberships));
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
        Map<Long, String> nutritionNames = nutritionPlanService.getPlanCatalog().stream()
                .collect(Collectors.toMap(NutritionPlanCatalogItemDto::getId, NutritionPlanCatalogItemDto::getPlanName, (a, b) -> a));

        Map<Long, String> trainerNames = trainerManagementService.getDirectory(0, 100, null, null, null).getItems().stream()
                .flatMap(t -> {
                    if (t.trainerId() == null || t.trainerId().isBlank()) {
                        return Stream.empty();
                    }
                    try {
                        long id = Long.parseLong(t.trainerId().trim());
                        return Stream.of(Map.entry(id, t.firstName() + " " + t.lastName()));
                    } catch (NumberFormatException e) {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

        Map<Long, String> groupClassNames = groupClassService.listAllGroupClasses().stream()
                .collect(Collectors.toMap(GroupClassResponse::id, GroupClassResponse::name, (a, b) -> a));

        model.addAttribute("nutritionNames", nutritionNames);
        model.addAttribute("trainerNames", trainerNames);
        model.addAttribute("groupClassNames", groupClassNames);
    }

    private static MembershipResponse resolveActiveMembership(List<MembershipResponse> memberships) {
        LocalDate today = LocalDate.now();
        return memberships.stream()
                .filter(m -> m.status() == MembershipStatus.ACTIVE && !m.endDate().isBefore(today))
                .max(Comparator.comparing(MembershipResponse::endDate))
                .orElse(null);
    }
}