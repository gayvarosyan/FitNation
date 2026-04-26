package com.example.fitnationprogress.validation;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationprogress.dto.CreateUserProgressEntryRequest;
import com.example.fitnationprogress.dto.UpdateUserProgressEntryRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
public class UserProgressValidator {

    public void validateForCreate(CreateUserProgressEntryRequest request) {
        validateRecordedAt(request.recordedAt());
        validateAtLeastOneMetric(
                request.weight(),
                request.bodyFatPercent(),
                request.muscleMass(),
                request.waistCm(),
                request.chestCm(),
                request.hipCm());
    }

    public void validateForUpdate(UpdateUserProgressEntryRequest request) {
        validateRecordedAt(request.recordedAt());
        validateAtLeastOneMetric(
                request.weight(),
                request.bodyFatPercent(),
                request.muscleMass(),
                request.waistCm(),
                request.chestCm(),
                request.hipCm());
    }

    public BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(ApplicationConstants.METRIC_SCALE, RoundingMode.HALF_UP);
    }

    private void validateRecordedAt(LocalDateTime recordedAt) {
        LocalDateTime maxAllowed = LocalDateTime.now().plusMinutes(ApplicationConstants.ALLOWED_FUTURE_MINUTES);
        if (recordedAt.isAfter(maxAllowed)) {
            throw new IllegalArgumentException(ApplicationConstants.PROGRESS_RECORDED_AT_TOO_FAR_FUTURE);
        }
    }

    private void validateAtLeastOneMetric(
            BigDecimal weight,
            BigDecimal bodyFatPercent,
            BigDecimal muscleMass,
            BigDecimal waistCm,
            BigDecimal chestCm,
            BigDecimal hipCm) {
        boolean allNull = weight == null
                && bodyFatPercent == null
                && muscleMass == null
                && waistCm == null
                && chestCm == null
                && hipCm == null;
        if (allNull) {
            throw new IllegalArgumentException(ApplicationConstants.PROGRESS_ENTRY_REQUIRED_METRIC);
        }
    }
}
