package com.example.fitnationweb.service;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationprogress.dto.ProgressEntryResponse;
import com.example.fitnationprogress.dto.ProgressSummaryResponse;
import com.example.fitnationprogress.dto.UpsertUserProgressEntryRequest;
import com.example.fitnationprogress.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressWebService {

    private final UserProgressService userProgressService;

    public List<ProgressEntryResponse> getMyHistory(Long userId) {
        return userProgressService
                .getMyHistory(userId, PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "recordedAt")))
                .getContent();
    }

    public ProgressSummaryResponse getMySummary(Long userId) {
        return userProgressService.getSummary(userId);
    }

    public void createMyEntry(Long userId, String recordedAt, BigDecimal weight, BigDecimal bodyFatPercent,
                              BigDecimal muscleMass, BigDecimal waistCm, BigDecimal chestCm, BigDecimal hipCm,
                              String notes) {
        userProgressService.createEntry(
                userId,
                toRequest(recordedAt, weight, bodyFatPercent, muscleMass, waistCm, chestCm, hipCm, notes));
    }

    public void updateMyEntry(Long userId, Long entryId, String recordedAt, BigDecimal weight, BigDecimal bodyFatPercent,
                              BigDecimal muscleMass, BigDecimal waistCm, BigDecimal chestCm, BigDecimal hipCm,
                              String notes) {
        userProgressService.updateMyEntry(
                userId,
                entryId,
                toRequest(recordedAt, weight, bodyFatPercent, muscleMass, waistCm, chestCm, hipCm, notes));
    }

    public void deleteMyEntry(Long userId, Long entryId) {
        userProgressService.deleteMyEntry(userId, entryId);
    }

    public List<ProgressEntryResponse> getClientHistoryForAdmin(Long clientUserId) {
        return userProgressService
                .getUserHistoryByActor(
                        null,
                        UserRole.ADMIN,
                        clientUserId,
                        PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "recordedAt")))
                .getContent();
    }

    public List<ProgressEntryResponse> getClientHistoryForTrainer(Long trainerUserId, UserRole trainerRole, Long clientUserId) {
        return userProgressService
                .getUserHistoryByActor(
                        trainerUserId,
                        trainerRole,
                        clientUserId,
                        PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "recordedAt")))
                .getContent();
    }

    public ProgressSummaryResponse getClientSummaryForAdmin(Long actorUserId, UserRole actorRole, Long clientUserId) {
        return userProgressService.getSummaryByActor(actorUserId, actorRole, clientUserId);
    }

    private static UpsertUserProgressEntryRequest toRequest(
            String recordedAt,
            BigDecimal weight,
            BigDecimal bodyFatPercent,
            BigDecimal muscleMass,
            BigDecimal waistCm,
            BigDecimal chestCm,
            BigDecimal hipCm,
            String notes) {
        return new UpsertUserProgressEntryRequest(
                LocalDateTime.parse(recordedAt),
                weight,
                bodyFatPercent,
                muscleMass,
                waistCm,
                chestCm,
                hipCm,
                notes);
    }
}
