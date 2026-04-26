package com.example.fitnationbooking.validation;

import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationcommon.exception.ClassBookingConflictException;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassBookingValidator {

    private final ClassBookingRepository classBookingRepository;

    public void validateCanBook(ClassSchedule schedule, User user) {
        validateCapacity(schedule);
        validateNoDuplicateBooking(schedule, user);
    }

    private void validateCapacity(ClassSchedule schedule) {
        long bookedCount = classBookingRepository.countByScheduleAndStatus(schedule.getId(), ClassBookingStatus.BOOKED.name());
        Integer capacity = schedule.getGroupClass().getCapacity();
        if (capacity != null && bookedCount >= capacity) {
            throw new ClassBookingConflictException(ApplicationConstants.CLASS_SCHEDULE_FULL);
        }
    }

    private void validateNoDuplicateBooking(ClassSchedule schedule, User user) {
        if (classBookingRepository.existsByScheduleAndUserAndStatus(schedule, user, ClassBookingStatus.BOOKED)) {
            throw new ClassBookingConflictException(ApplicationConstants.CLASS_ALREADY_BOOKED);
        }
    }
}