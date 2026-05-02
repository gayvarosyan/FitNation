package com.example.fitnationrestapi.service;

import com.example.fitnationbooking.service.ClassBookingService;
import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.ClassScheduleFilterRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.ScheduleFilterStatus;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBookingFacadeService {

    private final ClassBookingService classBookingService;
    private final GroupClassService groupClassService;
    private final CurrentUserHelper currentUserHelper;

    public List<ClassScheduleItemResponse> getAvailableClasses(Long trainerId,
                                                                LocalDate fromDate,
                                                                LocalDate toDate,
                                                                ScheduleFilterStatus status) {
        return groupClassService.getAllSchedules(new ClassScheduleFilterRequest(trainerId, fromDate, toDate, status));
    }

    public void bookClass(Long scheduleId) {
        classBookingService.bookClass(scheduleId, currentUserHelper.getId());
    }

    public void cancelBooking(Long bookingId) {
        classBookingService.cancelBooking(bookingId, currentUserHelper.getId());
    }

    public PagedResponse<UserBookingItemResponse> getUserBookings(Integer page, Integer size, String sort, String status) {
        return classBookingService.getUserBookings(currentUserHelper.getId(), page, size, sort, status);
    }
}
