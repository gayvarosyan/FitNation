package com.example.fitnationweb.service;

import com.example.fitnationbooking.service.ClassBookingService;
import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.ClassScheduleFilterRequest;
import com.example.fitnationcommon.dto.request.SubmitMembershipRequest;
import com.example.fitnationcommon.dto.response.GroupClassResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationcommon.enums.ScheduleFilterStatus;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.fitnationnutrition.service.NutritionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserPortalService {

    private final MembershipService membershipService;
    private final NutritionPlanService nutritionPlanService;
    private final TrainerManagementService trainerManagementService;
    private final GroupClassService groupClassService;
    private final ClassBookingService classBookingService;

    public void populateSubscriptionsModel(Model model, String email) {
        var pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "startDate"));
        var myMemberships = membershipService.getUserMemberships(email, pageable).getContent();

        model.addAttribute("plans", membershipService.getAllMembershipTypes());
        model.addAttribute("myRequests", membershipService.getUserMembershipRequests(email));
        model.addAttribute("myMemberships", myMemberships);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("activeMembership", resolveActiveMembership(myMemberships));

        Map<Long, String> nutritionNames = nutritionPlanService.getPlanCatalog().stream()
                .collect(Collectors.toMap(NutritionPlanCatalogItemDto::getId, NutritionPlanCatalogItemDto::getPlanName, (a, b) -> a));
        model.addAttribute("nutritionNames", nutritionNames);

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
        model.addAttribute("trainerNames", trainerNames);

        Map<Long, String> groupClassNames = groupClassService.listAllGroupClasses().stream()
                .collect(Collectors.toMap(GroupClassResponse::id, GroupClassResponse::name, (a, b) -> a));
        model.addAttribute("groupClassNames", groupClassNames);
    }

    public void submitSubscriptionRequest(String email, Long membershipTypeId) {
        membershipService.submitMembershipRequest(email, new SubmitMembershipRequest(membershipTypeId));
    }

    public void populateBookingsModel(
            Model model,
            Long userId,
            Long trainerId,
            LocalDate fromDate,
            LocalDate toDate,
            ScheduleFilterStatus scheduleStatus,
            String bookingStatus) {
        var schedules = groupClassService.getAllSchedules(
                new ClassScheduleFilterRequest(trainerId, fromDate, toDate, scheduleStatus));
        var myBookings = classBookingService.getUserBookings(
                userId, 0, 100, "createdAt,desc", bookingStatus).getItems();

        model.addAttribute("availableSchedules", schedules);
        model.addAttribute("myBookings", myBookings);
        model.addAttribute("trainers", trainerManagementService.getDirectory(0, 100, null, null, null).getItems());
        model.addAttribute("scheduleStatuses", ScheduleFilterStatus.values());
        model.addAttribute("selectedTrainerId", trainerId);
        model.addAttribute("selectedFromDate", fromDate);
        model.addAttribute("selectedToDate", toDate);
        model.addAttribute("selectedScheduleStatus", scheduleStatus);
        model.addAttribute("selectedBookingStatus", bookingStatus);
    }

    public void bookClass(Long scheduleId, Long userId) {
        classBookingService.bookClass(scheduleId, userId);
    }

    public void cancelBooking(Long bookingId, Long userId) {
        classBookingService.cancelBooking(bookingId, userId);
    }

    private static MembershipResponse resolveActiveMembership(java.util.List<MembershipResponse> memberships) {
        LocalDate today = LocalDate.now();
        return memberships.stream()
                .filter(m -> m.status() == MembershipStatus.ACTIVE && !m.endDate().isBefore(today))
                .max(Comparator.comparing(MembershipResponse::endDate))
                .orElse(null);
    }
}
