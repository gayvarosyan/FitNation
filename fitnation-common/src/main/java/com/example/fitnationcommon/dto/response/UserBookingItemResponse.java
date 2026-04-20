package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.BookingDisplayStatus;
import com.example.fitnationcommon.enums.ClassBookingStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record UserBookingItemResponse(
        Long bookingId,
        String className,
        String trainerName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        ClassBookingStatus status,
        BookingDisplayStatus displayStatus
) {}