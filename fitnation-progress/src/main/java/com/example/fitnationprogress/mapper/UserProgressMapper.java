package com.example.fitnationprogress.mapper;

import com.example.fitnationprogress.dto.CreateUserProgressEntryRequest;
import com.example.fitnationprogress.dto.ProgressEntryResponse;
import com.example.fitnationprogress.model.UserProgressEntry;
import com.example.fitnationuser.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserProgressMapper {

    public UserProgressEntry toEntity(User user, CreateUserProgressEntryRequest request) {
        return UserProgressEntry.builder()
                .user(user)
                .recordedAt(request.recordedAt())
                .weight(request.weight())
                .bodyFatPercent(request.bodyFatPercent())
                .muscleMass(request.muscleMass())
                .waistCm(request.waistCm())
                .chestCm(request.chestCm())
                .hipCm(request.hipCm())
                .notes(request.notes())
                .build();
    }

    public ProgressEntryResponse toResponse(UserProgressEntry entry) {
        return new ProgressEntryResponse(
                entry.getId(),
                entry.getUser().getId(),
                entry.getRecordedAt(),
                entry.getWeight(),
                entry.getBodyFatPercent(),
                entry.getMuscleMass(),
                entry.getWaistCm(),
                entry.getChestCm(),
                entry.getHipCm(),
                entry.getNotes(),
                entry.getCreatedAt(),
                entry.getUpdatedAt());
    }
}
