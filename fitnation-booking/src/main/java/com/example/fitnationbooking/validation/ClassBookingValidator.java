package com.example.fitnationbooking.validation;

import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationcommon.enums.ClassBookingStatus;
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
        long bookedCount = classBookingRepository.countByScheduleAndStatus(schedule, ClassBookingStatus.BOOKED);
        Integer capacity = schedule.getGroupClass().getCapacity();
        if (capacity != null && bookedCount >= capacity) {
            throw new IllegalStateException("Class is full");
        }
    }

    private void validateNoDuplicateBooking(ClassSchedule schedule, User user) {
        if (classBookingRepository.existsByScheduleAndUserAndStatus(schedule, user, ClassBookingStatus.BOOKED)) {
            throw new IllegalStateException("You already booked this class");
        }
    }
}
