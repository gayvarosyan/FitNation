package com.example.fitnationprogress.factory;

import com.example.fitnationprogress.constants.NotificationContextKeys;
import com.example.fitnationprogress.dto.NotificationTriggerCommand;
import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;

import java.util.HashMap;
import java.util.Map;

public final class NotificationCommandFactory {

    private NotificationCommandFactory() {
    }

    public static NotificationTriggerCommand membershipExpiringSoon(
            Long membershipId,
            Long subjectUserId,
            String membershipTypeName,
            int daysUntilExpiry) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.SUBJECT_USER_ID, String.valueOf(subjectUserId));
        ctx.put(NotificationContextKeys.MEMBERSHIP_TYPE_NAME, safe(membershipTypeName));
        ctx.put(NotificationContextKeys.DAYS_UNTIL_EXPIRY, String.valueOf(daysUntilExpiry));
        return NotificationTriggerCommand.of(
                NotificationEventType.MEMBERSHIP_EXPIRING_SOON,
                NotificationEntityType.MEMBERSHIP,
                membershipId,
                ctx);
    }

    public static NotificationTriggerCommand membershipRequestSubmitted(
            Long requestId,
            String membershipTypeName) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.MEMBERSHIP_TYPE_NAME, safe(membershipTypeName));
        return NotificationTriggerCommand.of(
                NotificationEventType.MEMBERSHIP_REQUEST_SUBMITTED,
                NotificationEntityType.MEMBERSHIP_REQUEST,
                requestId,
                ctx);
    }

    public static NotificationTriggerCommand membershipRequestApproved(
            Long requestId,
            Long subjectUserId,
            String membershipTypeName) {
        Map<String, String> ctx = baseMembershipRequestResult(subjectUserId, membershipTypeName);
        return NotificationTriggerCommand.of(
                NotificationEventType.MEMBERSHIP_REQUEST_APPROVED,
                NotificationEntityType.MEMBERSHIP_REQUEST,
                requestId,
                ctx);
    }

    public static NotificationTriggerCommand membershipRequestRejected(
            Long requestId,
            Long subjectUserId,
            String membershipTypeName,
            String rejectionReason) {
        Map<String, String> ctx = baseMembershipRequestResult(subjectUserId, membershipTypeName);
        ctx.put(NotificationContextKeys.REJECTION_REASON, safe(rejectionReason));
        return NotificationTriggerCommand.of(
                NotificationEventType.MEMBERSHIP_REQUEST_REJECTED,
                NotificationEntityType.MEMBERSHIP_REQUEST,
                requestId,
                ctx);
    }

    public static NotificationTriggerCommand membershipPurchaseConfirmed(
            Long membershipId,
            Long subjectUserId,
            String membershipTypeName) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.SUBJECT_USER_ID, String.valueOf(subjectUserId));
        ctx.put(NotificationContextKeys.MEMBERSHIP_TYPE_NAME, safe(membershipTypeName));
        return NotificationTriggerCommand.of(
                NotificationEventType.MEMBERSHIP_PURCHASE_CONFIRMED,
                NotificationEntityType.MEMBERSHIP,
                membershipId,
                ctx);
    }

    public static NotificationTriggerCommand freezeRequestSubmitted(
            Long freezeRequestId,
            String membershipTypeName,
            String freezeStart,
            String freezeEnd) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.MEMBERSHIP_TYPE_NAME, safe(membershipTypeName));
        ctx.put(NotificationContextKeys.FREEZE_START, safe(freezeStart));
        ctx.put(NotificationContextKeys.FREEZE_END, safe(freezeEnd));
        return NotificationTriggerCommand.of(
                NotificationEventType.FREEZE_REQUEST_SUBMITTED,
                NotificationEntityType.FREEZE_REQUEST,
                freezeRequestId,
                ctx);
    }

    public static NotificationTriggerCommand freezeRequestApproved(
            Long freezeRequestId,
            Long subjectUserId,
            String membershipTypeName,
            String freezeStart,
            String freezeEnd) {
        Map<String, String> ctx = baseFreezeResult(subjectUserId, membershipTypeName, freezeStart, freezeEnd);
        return NotificationTriggerCommand.of(
                NotificationEventType.FREEZE_REQUEST_APPROVED,
                NotificationEntityType.FREEZE_REQUEST,
                freezeRequestId,
                ctx);
    }

    public static NotificationTriggerCommand freezeRequestRejected(
            Long freezeRequestId,
            Long subjectUserId,
            String membershipTypeName,
            String rejectionReason) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.SUBJECT_USER_ID, String.valueOf(subjectUserId));
        ctx.put(NotificationContextKeys.MEMBERSHIP_TYPE_NAME, safe(membershipTypeName));
        ctx.put(NotificationContextKeys.REJECTION_REASON, safe(rejectionReason));
        return NotificationTriggerCommand.of(
                NotificationEventType.FREEZE_REQUEST_REJECTED,
                NotificationEntityType.FREEZE_REQUEST,
                freezeRequestId,
                ctx);
    }

    public static NotificationTriggerCommand classBooked(
            Long bookingId,
            Long subjectUserId,
            String className,
            String scheduleDate,
            String startTime,
            String trainerName) {
        Map<String, String> ctx = baseClass(subjectUserId, className, scheduleDate, startTime, trainerName);
        return NotificationTriggerCommand.of(
                NotificationEventType.CLASS_BOOKED,
                NotificationEntityType.CLASS_BOOKING,
                bookingId,
                ctx);
    }

    public static NotificationTriggerCommand classCanceled(
            Long bookingId,
            Long subjectUserId,
            Long trainerUserId,
            String className,
            String scheduleDate,
            String startTime) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.SUBJECT_USER_ID, String.valueOf(subjectUserId));
        ctx.put(NotificationContextKeys.TRAINER_USER_ID, String.valueOf(trainerUserId));
        ctx.put(NotificationContextKeys.CLASS_NAME, safe(className));
        ctx.put(NotificationContextKeys.SCHEDULE_DATE, safe(scheduleDate));
        ctx.put(NotificationContextKeys.START_TIME, safe(startTime));
        ctx.put(NotificationContextKeys.TRAINER_NAME, "");
        return NotificationTriggerCommand.of(
                NotificationEventType.CLASS_CANCELED,
                NotificationEntityType.CLASS_BOOKING,
                bookingId,
                ctx);
    }

    public static NotificationTriggerCommand classFull(
            Long scheduleId,
            Long trainerUserId,
            String className,
            String scheduleDate,
            String startTime) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.TRAINER_USER_ID, String.valueOf(trainerUserId));
        ctx.put(NotificationContextKeys.CLASS_NAME, safe(className));
        ctx.put(NotificationContextKeys.SCHEDULE_DATE, safe(scheduleDate));
        ctx.put(NotificationContextKeys.START_TIME, safe(startTime));
        return NotificationTriggerCommand.of(
                NotificationEventType.CLASS_FULL,
                NotificationEntityType.CLASS_SCHEDULE,
                scheduleId,
                ctx);
    }

    public static NotificationTriggerCommand trainerAssignmentRequested(
            Long requestId,
            Long trainerUserId,
            String clientName) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.TRAINER_USER_ID, String.valueOf(trainerUserId));
        ctx.put(NotificationContextKeys.CLIENT_NAME, safe(clientName));
        return NotificationTriggerCommand.of(
                NotificationEventType.TRAINER_ASSIGNMENT_REQUESTED,
                NotificationEntityType.TRAINER_ASSIGNMENT_REQUEST,
                requestId,
                ctx);
    }

    public static NotificationTriggerCommand trainerAssignmentApproved(
            Long requestId,
            Long subjectUserId,
            String trainerName) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.SUBJECT_USER_ID, String.valueOf(subjectUserId));
        ctx.put(NotificationContextKeys.TRAINER_NAME, safe(trainerName));
        return NotificationTriggerCommand.of(
                NotificationEventType.TRAINER_ASSIGNMENT_APPROVED,
                NotificationEntityType.TRAINER_ASSIGNMENT_REQUEST,
                requestId,
                ctx);
    }

    public static NotificationTriggerCommand trainerAssignmentRejected(
            Long requestId,
            Long subjectUserId,
            String trainerName) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.SUBJECT_USER_ID, String.valueOf(subjectUserId));
        ctx.put(NotificationContextKeys.TRAINER_NAME, safe(trainerName));
        return NotificationTriggerCommand.of(
                NotificationEventType.TRAINER_ASSIGNMENT_REJECTED,
                NotificationEntityType.TRAINER_ASSIGNMENT_REQUEST,
                requestId,
                ctx);
    }

    private static Map<String, String> baseMembershipRequestResult(Long subjectUserId, String membershipTypeName) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.SUBJECT_USER_ID, String.valueOf(subjectUserId));
        ctx.put(NotificationContextKeys.MEMBERSHIP_TYPE_NAME, safe(membershipTypeName));
        return ctx;
    }

    private static Map<String, String> baseFreezeResult(
            Long subjectUserId,
            String membershipTypeName,
            String freezeStart,
            String freezeEnd) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.SUBJECT_USER_ID, String.valueOf(subjectUserId));
        ctx.put(NotificationContextKeys.MEMBERSHIP_TYPE_NAME, safe(membershipTypeName));
        ctx.put(NotificationContextKeys.FREEZE_START, safe(freezeStart));
        ctx.put(NotificationContextKeys.FREEZE_END, safe(freezeEnd));
        return ctx;
    }

    private static Map<String, String> baseClass(
            Long subjectUserId,
            String className,
            String scheduleDate,
            String startTime,
            String trainerName) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(NotificationContextKeys.SUBJECT_USER_ID, String.valueOf(subjectUserId));
        ctx.put(NotificationContextKeys.CLASS_NAME, safe(className));
        ctx.put(NotificationContextKeys.SCHEDULE_DATE, safe(scheduleDate));
        ctx.put(NotificationContextKeys.START_TIME, safe(startTime));
        ctx.put(NotificationContextKeys.TRAINER_NAME, safe(trainerName));
        return ctx;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
