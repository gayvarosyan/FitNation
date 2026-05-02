package com.example.fitnationprogress.service;

import com.example.fitnationprogress.config.NotificationProperties;
import com.example.fitnationprogress.dto.NotificationTriggerCommand;
import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;
import com.example.fitnationprogress.enums.NotificationSeverity;
import com.example.fitnationprogress.repository.InAppNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTriggerServiceTest {

    @Mock
    private NotificationRuleRegistry notificationRuleRegistry;
    @Mock
    private NotificationRecipientResolver notificationRecipientResolver;
    @Mock
    private NotificationTemplateRenderer notificationTemplateRenderer;
    @Mock
    private NotificationTransactionalIssuer notificationTransactionalIssuer;
    @Mock
    private NotificationProperties notificationProperties;
    @Mock
    private InAppNotificationRepository inAppNotificationRepository;

    private NotificationTriggerService notificationTriggerService;

    @BeforeEach
    void setUp() {
        notificationTriggerService = new NotificationTriggerService(
                notificationRuleRegistry,
                notificationRecipientResolver,
                notificationTemplateRenderer,
                notificationTransactionalIssuer,
                notificationProperties,
                inAppNotificationRepository);
    }

    @Test
    void skipsWhenNoRecipients() {
        NotificationRuleSegment segment = new NotificationRuleSegment(
                NotificationEventType.CLASS_BOOKED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.INFO,
                "t",
                "b");
        when(notificationRuleRegistry.segmentsFor(NotificationEventType.CLASS_BOOKED)).thenReturn(List.of(segment));
        when(notificationRecipientResolver.resolve(eq(RecipientAudience.SUBJECT_USER), any())).thenReturn(List.of());

        NotificationTriggerCommand command = NotificationTriggerCommand.of(
                NotificationEventType.CLASS_BOOKED,
                NotificationEntityType.CLASS_BOOKING,
                1L,
                Map.of("subjectUserId", "9"));

        notificationTriggerService.dispatch(command);
        verify(notificationTransactionalIssuer, never()).persistNotification(
                any(), any(), any(), any(), any(), any());
    }

    @Test
    void recordsDedupWhenDuplicateExists() {
        NotificationRuleSegment segment = new NotificationRuleSegment(
                NotificationEventType.CLASS_BOOKED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.INFO,
                "Title",
                "Body");
        when(notificationRuleRegistry.segmentsFor(NotificationEventType.CLASS_BOOKED)).thenReturn(List.of(segment));
        when(notificationRecipientResolver.resolve(eq(RecipientAudience.SUBJECT_USER), any()))
                .thenReturn(List.of(9L));
        when(notificationProperties.resolveDedupWindow(any())).thenReturn(Duration.ofHours(1));
        when(notificationProperties.getMaxNotificationsPerRecipientPerHour()).thenReturn(80);
        when(inAppNotificationRepository.countByRecipientSince(eq(9L), any())).thenReturn(0L);
        when(inAppNotificationRepository.countByRecipientAndEventAndEntitySince(
                eq(9L),
                eq(NotificationEventType.CLASS_BOOKED),
                eq(NotificationEntityType.CLASS_BOOKING),
                eq(5L),
                any())).thenReturn(1L);

        NotificationTriggerCommand command = NotificationTriggerCommand.of(
                NotificationEventType.CLASS_BOOKED,
                NotificationEntityType.CLASS_BOOKING,
                5L,
                Map.of("subjectUserId", "9"));

        notificationTriggerService.dispatch(command);
        verify(notificationTransactionalIssuer, never()).persistNotification(
                any(), any(), any(), any(), any(), any());
    }

    @Test
    void issuesWhenEligible() {
        NotificationRuleSegment segment = new NotificationRuleSegment(
                NotificationEventType.CLASS_BOOKED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.INFO,
                "T",
                "B");
        when(notificationRuleRegistry.segmentsFor(NotificationEventType.CLASS_BOOKED)).thenReturn(List.of(segment));
        when(notificationRecipientResolver.resolve(eq(RecipientAudience.SUBJECT_USER), any()))
                .thenReturn(List.of(9L));
        when(notificationProperties.resolveDedupWindow(any())).thenReturn(Duration.ofHours(1));
        when(notificationProperties.getMaxNotificationsPerRecipientPerHour()).thenReturn(80);
        when(inAppNotificationRepository.countByRecipientSince(eq(9L), any())).thenReturn(0L);
        when(inAppNotificationRepository.countByRecipientAndEventAndEntitySince(
                eq(9L),
                eq(NotificationEventType.CLASS_BOOKED),
                eq(NotificationEntityType.CLASS_BOOKING),
                eq(5L),
                any())).thenReturn(0L);
        when(notificationTemplateRenderer.render(eq("T"), any())).thenReturn("T");
        when(notificationTemplateRenderer.render(eq("B"), any())).thenReturn("B");
        when(notificationTransactionalIssuer.buildMetadataJson(any(), any(), any(), any()))
                .thenReturn("{}");

        NotificationTriggerCommand command = NotificationTriggerCommand.of(
                NotificationEventType.CLASS_BOOKED,
                NotificationEntityType.CLASS_BOOKING,
                5L,
                Map.of("subjectUserId", "9"));

        notificationTriggerService.dispatch(command);
        verify(notificationTransactionalIssuer).persistNotification(
                eq(9L),
                ArgumentMatchers.same(command),
                eq(segment),
                eq("T"),
                eq("B"),
                eq("{}"));
    }
}
