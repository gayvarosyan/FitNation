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
        var booking = new ClassBooking();
        booking.setSchedule(schedule);
        booking.setUser(user);
        booking.setStatus(ClassBookingStatus.BOOKED);
        return booking;
    }

    public UserBookingItemResponse toUserBookingItemResponse(ClassBooking booking) {
        var schedule = booking.getSchedule();
        var groupClass = schedule.getGroupClass();
        var trainer = groupClass.getTrainer();
        var trainerName = trainer.getFirstName() + " " + trainer.getLastName();

        var displayStatus = computeDisplayStatus(
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
        var now = LocalDateTime.now();
        var sessionStart = LocalDateTime.of(date, startTime);
        var sessionEnd = LocalDateTime.of(date, endTime);

        if (now.isBefore(sessionStart)) {
            return BookingDisplayStatus.UPCOMING;
        }
        if (!now.isBefore(sessionStart) && now.isBefore(sessionEnd)) {
            return BookingDisplayStatus.IN_PROGRESS;
        }
        return BookingDisplayStatus.COMPLETED;
    }
}