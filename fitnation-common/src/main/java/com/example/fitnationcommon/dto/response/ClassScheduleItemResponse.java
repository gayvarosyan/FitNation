package com.example.fitnationcommon.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record ClassScheduleItemResponse(
        Long scheduleId,
        Long classId,
        String className,
        String classDescription,
        Long trainerId,
        String trainerName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Integer capacity,
        long bookedCount,
        int spotsLeft,
        boolean isFull
) {}