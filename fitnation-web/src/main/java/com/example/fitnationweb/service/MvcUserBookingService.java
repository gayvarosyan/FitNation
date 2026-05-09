package com.example.fitnationweb.service;

import com.example.fitnationbooking.service.ClassBookingService;
import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.ClassScheduleFilterRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.ScheduleFilterStatus;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MvcUserBookingService {

    private static final String BOOKINGS_PATH = "/users/bookings";

    private final ClassBookingService classBookingService;
    private final GroupClassService groupClassService;
    private final MvcValidationService validationService;

    public List<ClassScheduleItemResponse> getAvailableSchedules(
            Long trainerId,
            LocalDate fromDate,
            LocalDate toDate,
            ScheduleFilterStatus status) {
        return groupClassService.getAllSchedules(new ClassScheduleFilterRequest(trainerId, fromDate, toDate, status));
    }

    public void book(Long scheduleId, Long userId) {
        classBookingService.bookClass(scheduleId, userId);
    }

    public void cancel(Long bookingId, Long userId) {
        classBookingService.cancelBooking(bookingId, userId);
    }

    public PagedResponse<UserBookingItemResponse> listBookings(
            Long userId,
            Integer page,
            Integer size,
            String sort,
            String status) {
        int p = validationService.normalizePage(page);
        int s = validationService.normalizeSize(size);
        return classBookingService.getUserBookings(userId, p, s, sort, status);
    }

    public void populateAvailableClassesModel(
            Long trainerId,
            LocalDate fromDate,
            LocalDate toDate,
            ScheduleFilterStatus status,
            Model model) {
        model.addAttribute("classes", getAvailableSchedules(trainerId, fromDate, toDate, status));
        model.addAttribute("trainerId", trainerId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("status", status);
        model.addAttribute("navSection", "classes");
    }

    public void populateUserBookingsModel(Long userId, Integer page, Integer size, String sort, String status, Model model) {
        model.addAttribute("bookings", listBookings(userId, page, size, sort, status));
        model.addAttribute("statusFilter", status);
        model.addAttribute("navSection", "bookings");
    }

    public MvcRedirect bookForMvc(Long scheduleId, Long userId) {
        try {
            book(scheduleId, userId);
            return MvcRedirect.to(BOOKINGS_PATH, "Class booked successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(BOOKINGS_PATH, e.getMessage());
        }
    }

    public MvcRedirect cancelForMvc(Long bookingId, Long userId) {
        try {
            cancel(bookingId, userId);
            return MvcRedirect.to(BOOKINGS_PATH, "Booking cancelled successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(BOOKINGS_PATH, e.getMessage());
        }
    }
}

