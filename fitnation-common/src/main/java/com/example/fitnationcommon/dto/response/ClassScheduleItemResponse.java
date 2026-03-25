package com.example.fitnationcommon.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record ClassScheduleItemResponse(
        Long scheduleId,
        String className,
        String trainerName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Integer capacity
) {}