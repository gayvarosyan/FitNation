package com.example.fitnationweb.service;

import com.example.fitnationcommon.enums.ScheduleFilterStatus;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MvcUserPortalService {

    private static final String SUBSCRIPTIONS_PATH = "/portal/subscriptions";
    private static final String BOOKINGS_PATH = "/portal/bookings";

    private final UserPortalService userPortalService;

    public void populateSubscriptionsModel(Model model, String email) {
        userPortalService.populateSubscriptionsModel(model, email);
    }

    public MvcRedirect submitSubscriptionRequest(String email, Long membershipTypeId) {
        try {
            userPortalService.submitSubscriptionRequest(email, membershipTypeId);
            return MvcRedirect.to(SUBSCRIPTIONS_PATH, "Subscription request submitted.");
        } catch (Exception e) {
            return MvcRedirect.failure(SUBSCRIPTIONS_PATH, e.getMessage());
        }
    }

    public void populateBookingsModel(
            Model model,
            Long userId,
            Long trainerId,
            LocalDate fromDate,
            LocalDate toDate,
            ScheduleFilterStatus scheduleStatus,
            String bookingStatus) {
        userPortalService.populateBookingsModel(model, userId, trainerId, fromDate, toDate, scheduleStatus, bookingStatus);
    }

    public MvcRedirect book(Long scheduleId, Long userId) {
        try {
            userPortalService.bookClass(scheduleId, userId);
            return MvcRedirect.to(BOOKINGS_PATH, "Class booked successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(BOOKINGS_PATH, e.getMessage());
        }
    }

    public MvcRedirect cancel(Long bookingId, Long userId) {
        try {
            userPortalService.cancelBooking(bookingId, userId);
            return MvcRedirect.to(BOOKINGS_PATH, "Booking cancelled successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(BOOKINGS_PATH, e.getMessage());
        }
    }
}

