package com.example.fitnationbooking.mapper;

import com.example.fitnationbooking.entity.ClassBooking;
import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationuser.user.User;
import org.springframework.stereotype.Component;

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
        return new UserBookingItemResponse(
                booking.getId(),
                groupClass.getName(),
                trainerName,
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                booking.getStatus()
        );
    }
}
