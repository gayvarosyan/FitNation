package com.example.fitnationprogress.mapper;

import com.example.fitnationprogress.dto.InAppNotificationResponse;
import com.example.fitnationprogress.model.InAppNotification;
import org.springframework.stereotype.Component;

@Component
public class InAppNotificationMapper {

    public InAppNotificationResponse toResponse(InAppNotification entity) {
        return new InAppNotificationResponse(
                entity.getId(),
                entity.getRecipient().getId(),
                entity.getEventType(),
                entity.getTitle(),
                entity.getBody(),
                entity.isRead(),
                entity.getCreatedAt(),
                entity.getMetadataJson(),
                entity.getSeverity());
    }
}
