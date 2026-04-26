package com.example.fitnationprogress.dto;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateUserProgressEntryRequest(

        @NotNull
        LocalDateTime recordedAt,

        @DecimalMin(ApplicationConstants.WEIGHT_MIN_STR)
        @DecimalMax(ApplicationConstants.WEIGHT_MAX_STR)
        @Digits(integer = 3, fraction = ApplicationConstants.METRIC_SCALE)
        BigDecimal weight,

        @DecimalMin(ApplicationConstants.BODY_FAT_MIN_STR)
        @DecimalMax(ApplicationConstants.BODY_FAT_MAX_STR)
        @Digits(integer = 2, fraction = ApplicationConstants.METRIC_SCALE)
        BigDecimal bodyFatPercent,

        @DecimalMin(ApplicationConstants.MUSCLE_MASS_MIN_STR)
        @DecimalMax(ApplicationConstants.MUSCLE_MASS_MAX_STR)
        @Digits(integer = 3, fraction = ApplicationConstants.METRIC_SCALE)
        BigDecimal muscleMass,

        @DecimalMin(ApplicationConstants.MEASUREMENT_MIN_STR)
        @DecimalMax(ApplicationConstants.MEASUREMENT_MAX_STR)
        @Digits(integer = 3, fraction = ApplicationConstants.METRIC_SCALE)
        BigDecimal waistCm,

        @DecimalMin(ApplicationConstants.MEASUREMENT_MIN_STR)
        @DecimalMax(ApplicationConstants.MEASUREMENT_MAX_STR)
        @Digits(integer = 3, fraction = ApplicationConstants.METRIC_SCALE)
        BigDecimal chestCm,

        @DecimalMin(ApplicationConstants.MEASUREMENT_MIN_STR)
        @DecimalMax(ApplicationConstants.MEASUREMENT_MAX_STR)
        @Digits(integer = 3, fraction = ApplicationConstants.METRIC_SCALE)
        BigDecimal hipCm,

        @Size(max = ApplicationConstants.NOTES_MAX_LENGTH)
        String notes
) {}
