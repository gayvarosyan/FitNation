package com.example.fitnationprogress.service;

import com.example.fitnationprogress.enums.NotificationEventType;
import com.example.fitnationprogress.enums.NotificationSeverity;
import com.example.fitnationprogress.exception.UnknownNotificationEventException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class NotificationRuleRegistry {

    private static final Map<NotificationEventType, List<NotificationRuleSegment>> RULES = buildRules();

    public List<NotificationRuleSegment> segmentsFor(NotificationEventType eventType) {
        List<NotificationRuleSegment> segments = RULES.get(eventType);
        if (segments == null) {
            throw new UnknownNotificationEventException(eventType);
        }
        return segments;
    }

    private static Map<NotificationEventType, List<NotificationRuleSegment>> buildRules() {
        Map<NotificationEventType, List<NotificationRuleSegment>> map = new EnumMap<>(NotificationEventType.class);

        map.put(NotificationEventType.MEMBERSHIP_EXPIRING_SOON, List.of(seg(
                NotificationEventType.MEMBERSHIP_EXPIRING_SOON,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.WARNING,
                "Membership expiring soon",
                "Your membership {{membershipTypeName}} expires in {{daysUntilExpiry}} day(s).")));

        map.put(NotificationEventType.MEMBERSHIP_REQUEST_SUBMITTED, List.of(seg(
                NotificationEventType.MEMBERSHIP_REQUEST_SUBMITTED,
                RecipientAudience.ALL_ACTIVE_ADMINS,
                NotificationSeverity.INFO,
                "New membership request",
                "A client submitted a membership request for {{membershipTypeName}}.")));

        map.put(NotificationEventType.MEMBERSHIP_REQUEST_APPROVED, List.of(seg(
                NotificationEventType.MEMBERSHIP_REQUEST_APPROVED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.INFO,
                "Membership request approved",
                "Your membership request for {{membershipTypeName}} was approved.")));

        map.put(NotificationEventType.MEMBERSHIP_REQUEST_REJECTED, List.of(seg(
                NotificationEventType.MEMBERSHIP_REQUEST_REJECTED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.WARNING,
                "Membership request rejected",
                "Your membership request for {{membershipTypeName}} was rejected. Reason: {{rejectionReason}}.")));

        map.put(NotificationEventType.MEMBERSHIP_PURCHASE_CONFIRMED, List.of(seg(
                NotificationEventType.MEMBERSHIP_PURCHASE_CONFIRMED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.INFO,
                "Membership activated",
                "Your membership {{membershipTypeName}} is now active.")));

        map.put(NotificationEventType.FREEZE_REQUEST_SUBMITTED, List.of(seg(
                NotificationEventType.FREEZE_REQUEST_SUBMITTED,
                RecipientAudience.ALL_ACTIVE_ADMINS,
                NotificationSeverity.INFO,
                "Freeze request submitted",
                "A client submitted a freeze request for membership {{membershipTypeName}} ({{freezeStart}} – {{freezeEnd}}).")));

        map.put(NotificationEventType.FREEZE_REQUEST_APPROVED, List.of(seg(
                NotificationEventType.FREEZE_REQUEST_APPROVED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.INFO,
                "Freeze request approved",
                "Your freeze request for {{membershipTypeName}} from {{freezeStart}} to {{freezeEnd}} was approved.")));

        map.put(NotificationEventType.FREEZE_REQUEST_REJECTED, List.of(seg(
                NotificationEventType.FREEZE_REQUEST_REJECTED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.WARNING,
                "Freeze request rejected",
                "Your freeze request for {{membershipTypeName}} was rejected. Reason: {{rejectionReason}}.")));

        map.put(NotificationEventType.CLASS_BOOKED, List.of(seg(
                NotificationEventType.CLASS_BOOKED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.INFO,
                "Class booked",
                "You are booked for {{className}} on {{scheduleDate}} at {{startTime}} with {{trainerName}}.")));

        map.put(NotificationEventType.CLASS_CANCELED, List.of(
                seg(NotificationEventType.CLASS_CANCELED, RecipientAudience.SUBJECT_USER, NotificationSeverity.INFO,
                        "Class canceled",
                        "You canceled {{className}} on {{scheduleDate}} at {{startTime}}."),
                seg(NotificationEventType.CLASS_CANCELED, RecipientAudience.TRAINER_USER_AND_ADMINS,
                        NotificationSeverity.INFO,
                        "Seat freed after cancellation",
                        "A client canceled {{className}} on {{scheduleDate}} at {{startTime}}.")));

        map.put(NotificationEventType.CLASS_FULL, List.of(seg(
                NotificationEventType.CLASS_FULL,
                RecipientAudience.TRAINER_USER_AND_ADMINS,
                NotificationSeverity.WARNING,
                "Class is full",
                "{{className}} on {{scheduleDate}} at {{startTime}} reached full capacity.")));

        map.put(NotificationEventType.TRAINER_ASSIGNMENT_REQUESTED, List.of(seg(
                NotificationEventType.TRAINER_ASSIGNMENT_REQUESTED,
                RecipientAudience.TRAINER_USER,
                NotificationSeverity.INFO,
                "New trainer assignment request",
                "{{clientName}} requested to assign you as their trainer.")));

        map.put(NotificationEventType.TRAINER_ASSIGNMENT_APPROVED, List.of(seg(
                NotificationEventType.TRAINER_ASSIGNMENT_APPROVED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.INFO,
                "Trainer approved your request",
                "{{trainerName}} approved your assignment request.")));

        map.put(NotificationEventType.TRAINER_ASSIGNMENT_REJECTED, List.of(seg(
                NotificationEventType.TRAINER_ASSIGNMENT_REJECTED,
                RecipientAudience.SUBJECT_USER,
                NotificationSeverity.WARNING,
                "Trainer declined your request",
                "{{trainerName}} declined your assignment request.")));

        return Map.copyOf(map);
    }

    private static NotificationRuleSegment seg(
            NotificationEventType eventType,
            RecipientAudience audience,
            NotificationSeverity severity,
            String titleTemplate,
            String bodyTemplate) {
        return new NotificationRuleSegment(eventType, audience, severity, titleTemplate, bodyTemplate);
    }
}
