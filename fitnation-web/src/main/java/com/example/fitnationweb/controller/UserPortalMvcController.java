package com.example.fitnationweb.controller;

import com.example.fitnationcommon.enums.ScheduleFilterStatus;
import com.example.fitnationweb.service.MvcUserPortalService;
import com.example.fitnationweb.support.CurrentUserAccessor;
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

@Controller
@RequestMapping("/portal")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
public class UserPortalMvcController {

    private static final String SUBSCRIPTIONS_VIEW = "portal/subscriptions";
    private static final String BOOKINGS_VIEW = "portal/bookings";

    private final MvcUserPortalService mvcUserPortalService;
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
        mvcUserPortalService.populateSubscriptionsModel(model, email);
        return SUBSCRIPTIONS_VIEW;
    }

    @PostMapping("/subscriptions/request")
    public String requestSubscription(
            @RequestParam Long membershipTypeId,
            RedirectAttributes redirectAttributes) {
        var user = currentUserAccessor.requireUser();
        var result = mvcUserPortalService.submitSubscriptionRequest(user.getEmail(), membershipTypeId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @GetMapping("/bookings")
    public String bookings(
            @RequestParam(required = false) Long trainerId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) ScheduleFilterStatus scheduleStatus,
            @RequestParam(required = false) String bookingStatus,
            Model model) {
        var user = currentUserAccessor.requireUser();
        model.addAttribute("navSection", "bookings");
        mvcUserPortalService.populateBookingsModel(
                model, user.getId(), trainerId, fromDate, toDate, scheduleStatus, bookingStatus);
        return BOOKINGS_VIEW;
    }

    @PostMapping("/bookings/book")
    public String bookClass(@RequestParam Long scheduleId, RedirectAttributes redirectAttributes) {
        var user = currentUserAccessor.requireUser();
        var result = mvcUserPortalService.book(scheduleId, user.getId());
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/bookings/cancel")
    public String cancelBooking(@RequestParam Long bookingId, RedirectAttributes redirectAttributes) {
        var user = currentUserAccessor.requireUser();
        var result = mvcUserPortalService.cancel(bookingId, user.getId());
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

}