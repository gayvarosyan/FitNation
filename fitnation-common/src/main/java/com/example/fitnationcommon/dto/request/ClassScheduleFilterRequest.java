package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.enums.ScheduleFilterStatus;

import java.time.LocalDate;

public record ClassScheduleFilterRequest(
        Long trainerId,
        LocalDate fromDate,
        LocalDate toDate,
        ScheduleFilterStatus status
) {}