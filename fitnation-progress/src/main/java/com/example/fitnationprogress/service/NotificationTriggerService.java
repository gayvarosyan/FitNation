package com.example.fitnationprogress.service;

import com.example.fitnationprogress.config.NotificationProperties;
import com.example.fitnationprogress.dto.NotificationTriggerCommand;
import com.example.fitnationprogress.repository.InAppNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationTriggerService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationTriggerService.class);

    private final NotificationRuleRegistry notificationRuleRegistry;
    private final NotificationRecipientResolver notificationRecipientResolver;
    private final NotificationTemplateRenderer notificationTemplateRenderer;
    private final NotificationTransactionalIssuer notificationTransactionalIssuer;
    private final NotificationProperties notificationProperties;
    private final InAppNotificationRepository inAppNotificationRepository;

    public void dispatch(NotificationTriggerCommand command) {
        var segments = notificationRuleRegistry.segmentsFor(command.eventType());
        for (var segment : segments) {
            dispatchSegment(command, segment);
        }
    }

    private void dispatchSegment(
            NotificationTriggerCommand command,
            NotificationRuleSegment segment) {
        List<Long> recipientIds;
        try {
            recipientIds = notificationRecipientResolver.resolve(segment.audience(), command);
        } catch (RuntimeException ex) {
            LOG.warn("notification_rule failed_to_resolve_recipients eventType={} error={}",
                    command.eventType(), ex.getMessage());
            return;
        }

        if (recipientIds.isEmpty()) {
            LOG.info("notification_rule skipped_no_recipients eventType={}", command.eventType());
            return;
        }

        var burstWindowStart = Instant.now().minus(1, ChronoUnit.HOURS);
        var burstLimit = notificationProperties.getMaxNotificationsPerRecipientPerHour();
        var dedupWindow = notificationProperties.resolveDedupWindow(command.eventType());
        var dedupSince = Instant.now().minus(dedupWindow);

        for (var recipientId : recipientIds) {
            try {
                if (isBurstLimited(recipientId, burstWindowStart, burstLimit)) {
                    LOG.info("notification_rule skipped_burst eventType={} recipientId={}",
                            command.eventType(), recipientId);
                    continue;
                }
                if (isDeduped(recipientId, command, dedupSince)) {
                    LOG.info("notification_rule skipped_dedup eventType={} recipientId={}",
                            command.eventType(), recipientId);
                    continue;
                }
                var title = notificationTemplateRenderer.render(segment.titleTemplate(), command);
                var body = notificationTemplateRenderer.render(segment.bodyTemplate(), command);
                var metadataJson = notificationTransactionalIssuer.buildMetadataJson(
                        command, command.eventType(), command.entityType(), command.entityId());
                notificationTransactionalIssuer.persistNotification(
                        recipientId, command, segment, title, body, metadataJson);
            } catch (RuntimeException ex) {
                LOG.warn("notification_rule failed_to_issue eventType={} recipientId={} error={}",
                        command.eventType(), recipientId, ex.getMessage());
            }
        }
    }

    private boolean isBurstLimited(Long recipientId, Instant burstWindowStart, long burstLimit) {
        long recent = inAppNotificationRepository.countByRecipientSince(recipientId, burstWindowStart);
        return recent >= burstLimit;
    }

    private boolean isDeduped(Long recipientId, NotificationTriggerCommand command, Instant dedupSince) {
        return inAppNotificationRepository.countByRecipientAndEventAndEntitySince(
                recipientId,
                command.eventType(),
                command.entityType(),
                command.entityId(),
                dedupSince) > 0;
    }
}
