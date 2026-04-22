package com.example.fitnationbooking.mapper;

import com.example.fitnationbooking.entity.ClassBooking;
import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.BookingDisplayStatus;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationuser.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ClassBookingMapper {

    public ClassBooking toBookedEntity(ClassSchedule schedule, User user) {
        ClassBooking booking = new ClassBooking();
        booking.setSchedule(schedule);
        booking.setUser(user);
        booking.setStatus(ClassBookingStatus.BOOKED);
        return booking;
    }

    public UserBookingItemResponse toUserBookingItemResponse(ClassBooking booking) {
        ClassSchedule schedule = booking.getSchedule();
        var groupClass = schedule.getGroupClass();
        var trainer = groupClass.getTrainer();
        String trainerName = trainer.getFirstName() + " " + trainer.getLastName();

        BookingDisplayStatus displayStatus = computeDisplayStatus(
                booking.getStatus(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );

        return new UserBookingItemResponse(
                booking.getId(),
                groupClass.getName(),
                trainerName,
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                booking.getStatus(),
                displayStatus
        );
    }

    private BookingDisplayStatus computeDisplayStatus(
            ClassBookingStatus status,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        if (status == ClassBookingStatus.CANCELLED) {
            return BookingDisplayStatus.CANCELLED;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sessionStart = LocalDateTime.of(date, startTime);
        LocalDateTime sessionEnd = LocalDateTime.of(date, endTime);

        if (now.isBefore(sessionStart)) {
            return BookingDisplayStatus.UPCOMING;
        } else if (!now.isBefore(sessionStart) && now.isBefore(sessionEnd)) {
            return BookingDisplayStatus.IN_PROGRESS;
        } else {
            return BookingDisplayStatus.COMPLETED;
        }
    }
}