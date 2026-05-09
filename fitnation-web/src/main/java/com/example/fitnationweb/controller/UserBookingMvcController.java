package com.example.fitnationweb.controller;

import com.example.fitnationcommon.enums.ScheduleFilterStatus;
import com.example.fitnationweb.service.MvcUserBookingService;
import com.example.fitnationweb.support.CurrentUserAccessor;
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

import java.time.LocalDate;
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT', 'TRAINER', 'ADMIN')")
public class UserBookingMvcController {

    private final MvcUserBookingService mvcUserBookingService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping("/classes")
    public String getAvailableClasses(
            @RequestParam(required = false) Long trainerId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) ScheduleFilterStatus status,
            Model model) {
        try {
            mvcUserBookingService.populateAvailableClassesModel(trainerId, fromDate, toDate, status, model);
            return "user/available-classes";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/available-classes";
        }
    }

    @PostMapping("/classes/{scheduleId}/book")
    public String bookClass(@PathVariable Long scheduleId, RedirectAttributes redirectAttributes) {
        var result = mvcUserBookingService.bookForMvc(scheduleId, currentUserAccessor.requireUser().getId());
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = mvcUserBookingService.cancelForMvc(id, currentUserAccessor.requireUser().getId());
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @GetMapping("/bookings")
    public String getUserBookings(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String status,
            Model model) {
        try {
            mvcUserBookingService.populateUserBookingsModel(
                    currentUserAccessor.requireUser().getId(), page, size, sort, status, model);
            return "user/my-bookings";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/my-bookings";
        }
    }
}
