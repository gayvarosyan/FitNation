package com.example.fitnationprogress.service.impl;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationcommon.exception.ProgressEntryNotFoundException;
import com.example.fitnationprogress.dto.CreateUserProgressEntryRequest;
import com.example.fitnationprogress.dto.ProgressEntryResponse;
import com.example.fitnationprogress.dto.ProgressMetricDeltas;
import com.example.fitnationprogress.dto.ProgressPeriodTrend;
import com.example.fitnationprogress.dto.ProgressSummaryResponse;
import com.example.fitnationprogress.dto.UpdateUserProgressEntryRequest;
import com.example.fitnationprogress.mapper.UserProgressMapper;
import com.example.fitnationprogress.model.UserProgressEntry;
import com.example.fitnationprogress.repository.UserProgressEntryRepository;
import com.example.fitnationprogress.service.UserProgressService;
import com.example.fitnationprogress.validation.UserProgressValidator;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProgressServiceImpl implements UserProgressService {

    private final UserProgressEntryRepository entryRepository;
    private final UserRepository userRepository;
    private final UserProgressMapper mapper;
    private final UserProgressValidator validator;

    @Override
    @Transactional
    public ProgressEntryResponse createEntry(Long userId, CreateUserProgressEntryRequest request) {
        var user = requireUser(userId);
        validator.validateForCreate(request);
        var entity = mapper.toEntity(user, normalizeRequest(request));
        return mapper.toResponse(entryRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProgressEntryResponse> getMyHistory(Long userId, Pageable pageable) {
        return entryRepository.findAllActiveByUserId(userId, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressEntryResponse getMyEntry(Long userId, Long entryId) {
        var entry = requireOwnedEntry(userId, entryId);
        return mapper.toResponse(entry);
    }

    @Override
    @Transactional
    public ProgressEntryResponse updateMyEntry(Long userId, Long entryId, UpdateUserProgressEntryRequest request) {
        var entry = requireOwnedEntry(userId, entryId);
        validator.validateForUpdate(request);
        var normalized = normalizeRequest(request);
        entry.updateMetrics(
                normalized.recordedAt(),
                normalized.weight(),
                normalized.bodyFatPercent(),
                normalized.muscleMass(),
                normalized.waistCm(),
                normalized.chestCm(),
                normalized.hipCm(),
                normalized.notes());
        return mapper.toResponse(entryRepository.save(entry));
    }

    @Override
    @Transactional
    public void deleteMyEntry(Long userId, Long entryId) {
        var entry = requireOwnedEntry(userId, entryId);
        entry.markDeleted(LocalDateTime.now());
        entryRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressSummaryResponse getSummary(Long userId) {
        requireUser(userId);
        return buildSummary(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProgressEntryResponse> getUserHistoryByActor(
            Long actorUserId,
            UserRole actorRole,
            Long targetUserId,
            Pageable pageable) {
        assertCanReadTargetUser(actorUserId, actorRole, targetUserId);
        return entryRepository.findAllActiveByUserId(targetUserId, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressSummaryResponse getSummaryByActor(Long actorUserId, UserRole actorRole, Long targetUserId) {
        assertCanReadTargetUser(actorUserId, actorRole, targetUserId);
        return buildSummary(targetUserId);
    }

    private ProgressSummaryResponse buildSummary(Long userId) {
        var entries = entryRepository.findAllActiveByUserIdOrderByRecordedAtDesc(userId);
        var latest = entries.isEmpty() ? null : entries.getFirst();
        var previous = entries.size() > 1 ? entries.get(1) : null;

        var changeVsPrevious = latest == null
                ? null
                : buildDeltas(latest, previous);

        var trend7 = buildPeriodTrend(userId, 7);
        var trend30 = buildPeriodTrend(userId, 30);

        return new ProgressSummaryResponse(
                latest == null ? null : mapper.toResponse(latest),
                changeVsPrevious,
                trend7,
                trend30,
                entryRepository.countActiveByUserId(userId));
    }

    private ProgressPeriodTrend buildPeriodTrend(Long userId, int days) {
        var entries = entryRepository.findAllActiveByUserIdRecordedAfter(
                userId,
                LocalDateTime.now().minusDays(days));
        if (entries.size() < 2) {
            return new ProgressPeriodTrend(days, null);
        }
        var newest = entries.getFirst();
        var oldest = entries.getLast();
        return new ProgressPeriodTrend(days, buildDeltas(newest, oldest));
    }

    private ProgressMetricDeltas buildDeltas(UserProgressEntry current, UserProgressEntry compareTo) {
        if (compareTo == null) {
            return null;
        }
        return new ProgressMetricDeltas(
                delta(current.getWeight(), compareTo.getWeight()),
                delta(current.getBodyFatPercent(), compareTo.getBodyFatPercent()),
                delta(current.getMuscleMass(), compareTo.getMuscleMass()),
                delta(current.getWaistCm(), compareTo.getWaistCm()),
                delta(current.getChestCm(), compareTo.getChestCm()),
                delta(current.getHipCm(), compareTo.getHipCm()));
    }

    private BigDecimal delta(BigDecimal current, BigDecimal previous) {
        if (current == null || previous == null) {
            return null;
        }
        return current.subtract(previous);
    }

    private UserProgressEntry requireOwnedEntry(Long userId, Long entryId) {
        var entry = entryRepository.findActiveById(entryId)
                .orElseThrow(() -> new ProgressEntryNotFoundException(ApplicationConstants.PROGRESS_ENTRY_NOT_FOUND));
        if (!entry.getUser().getId().equals(userId)) {
            throw new ForbiddenOperationException(ApplicationConstants.PROGRESS_ENTRY_NOT_OWNER);
        }
        return entry;
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ApplicationConstants.PROGRESS_USER_NOT_FOUND));
    }

    private void assertCanReadTargetUser(Long actorUserId, UserRole actorRole, Long targetUserId) {
        if (actorRole == UserRole.ADMIN || actorUserId.equals(targetUserId)) {
            requireUser(targetUserId);
            return;
        }
        if (actorRole == UserRole.TRAINER) {
            var targetUser = requireUser(targetUserId);
            if (!actorUserId.equals(targetUser.getAssignedTrainerId())) {
                throw new ForbiddenOperationException(ApplicationConstants.TRAINER_NOT_ASSIGNED_TO_CLIENT);
            }
            return;
        }
        throw new ForbiddenOperationException(ApplicationConstants.PROGRESS_ENTRY_NOT_OWNER);
    }

    private CreateUserProgressEntryRequest normalizeRequest(CreateUserProgressEntryRequest request) {
        return new CreateUserProgressEntryRequest(
                request.recordedAt(),
                validator.normalize(request.weight()),
                validator.normalize(request.bodyFatPercent()),
                validator.normalize(request.muscleMass()),
                validator.normalize(request.waistCm()),
                validator.normalize(request.chestCm()),
                validator.normalize(request.hipCm()),
                request.notes());
    }

    private UpdateUserProgressEntryRequest normalizeRequest(UpdateUserProgressEntryRequest request) {
        return new UpdateUserProgressEntryRequest(
                request.recordedAt(),
                validator.normalize(request.weight()),
                validator.normalize(request.bodyFatPercent()),
                validator.normalize(request.muscleMass()),
                validator.normalize(request.waistCm()),
                validator.normalize(request.chestCm()),
                validator.normalize(request.hipCm()),
                request.notes());
    }
}
