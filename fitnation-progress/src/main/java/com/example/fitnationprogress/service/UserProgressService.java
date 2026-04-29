package com.example.fitnationprogress.service;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationprogress.dto.ProgressEntryResponse;
import com.example.fitnationprogress.dto.ProgressSummaryResponse;
import com.example.fitnationprogress.dto.UpsertUserProgressEntryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserProgressService {

    ProgressEntryResponse createEntry(Long userId, UpsertUserProgressEntryRequest request);

    Page<ProgressEntryResponse> getMyHistory(Long userId, Pageable pageable);

    ProgressEntryResponse getMyEntry(Long userId, Long entryId);

    ProgressEntryResponse updateMyEntry(Long userId, Long entryId, UpsertUserProgressEntryRequest request);

    void deleteMyEntry(Long userId, Long entryId);

    ProgressSummaryResponse getSummary(Long userId);

    Page<ProgressEntryResponse> getUserHistoryByActor(Long actorUserId, UserRole actorRole, Long targetUserId, Pageable pageable);

    ProgressSummaryResponse getSummaryByActor(Long actorUserId, UserRole actorRole, Long targetUserId);
}
