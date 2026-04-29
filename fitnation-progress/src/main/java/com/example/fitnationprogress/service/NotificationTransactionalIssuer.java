package com.example.fitnationprogress.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationprogress.dto.NotificationTriggerCommand;
import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;
import com.example.fitnationprogress.model.InAppNotification;
import com.example.fitnationprogress.repository.InAppNotificationRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class NotificationTransactionalIssuer {

    private final InAppNotificationRepository inAppNotificationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistNotification(
            Long recipientUserId,
            NotificationTriggerCommand command,
            NotificationRuleSegment segment,
            String title,
            String body,
            String metadataJson) {
        var entity = InAppNotification.builder()
                .recipient(userRepository.getReferenceById(recipientUserId))
                .eventType(command.eventType())
                .title(title)
                .body(body)
                .read(false)
                .metadataJson(metadataJson)
                .severity(segment.severity())
                .entityType(command.entityType())
                .entityId(command.entityId())
                .build();
        inAppNotificationRepository.save(entity);
    }

    public String buildMetadataJson(
            NotificationTriggerCommand command,
            NotificationEventType eventType,
            NotificationEntityType entityType,
            Long entityId) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("entityType", entityType.name());
        payload.put("entityId", entityId);
        payload.put("eventType", eventType.name());
        var actionUrl = command.context().get(com.example.fitnationprogress.constants.NotificationContextKeys.ACTION_URL);
        if (actionUrl != null && !actionUrl.isBlank()) {
            payload.put("actionUrl", actionUrl);
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(ApplicationConstants.NOTIFICATION_METADATA_SERIALIZATION_FAILED, e);
        }
    }
}
