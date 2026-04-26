package com.example.fitnationmembership.service.impl;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.response.AdminFreezeRequestResponse;
import com.example.fitnationcommon.dto.request.RejectFreezeRequest;
import com.example.fitnationcommon.dto.request.RenewMembershipRequest;
import com.example.fitnationcommon.dto.request.SubmitFreezeRequest;
import com.example.fitnationcommon.dto.response.UserFreezeRequestResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.enums.FreezeRequestStatus;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationcommon.enums.PaymentEntityType;
import com.example.fitnationcommon.enums.PaymentStatus;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.MembershipNotFoundException;
import com.example.fitnationcommon.exception.MembershipRequestConflictException;
import com.example.fitnationcommon.exception.MembershipRequestNotFoundException;
import com.example.fitnationmembership.mapper.MembershipFreezeMapper;
import com.example.fitnationmembership.mapper.MembershipMapper;
import com.example.fitnationmembership.model.Membership;
import com.example.fitnationmembership.model.MembershipFreezeRequests;
import com.example.fitnationmembership.repository.MembershipFreezeRequestRepository;
import com.example.fitnationmembership.repository.MembershipRepository;
import com.example.fitnationmembership.service.MembershipFreezeService;
import com.example.fitnationprogress.factory.NotificationCommandFactory;
import com.example.fitnationprogress.service.NotificationCommandPublisher;
import com.example.fitnationuser.payment.Payment;
import com.example.fitnationuser.payment.PaymentRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipFreezeServiceImpl implements MembershipFreezeService {

    private final MembershipRepository membershipRepository;
    private final MembershipFreezeRequestRepository freezeRequestRepository;
    private final PaymentRepository paymentRepository;
    private final MembershipMapper membershipMapper;
    private final MembershipFreezeMapper membershipFreezeMapper;
    private final NotificationCommandPublisher notificationCommandPublisher;

    @Override
    @Transactional
    public UserFreezeRequestResponse submitFreezeRequest(User currentUser, Long membershipId, SubmitFreezeRequest req) {
        Membership membership = requireMembership(membershipId);
        assertOwner(currentUser, membership, ApplicationConstants.FREEZE_NOT_OWNER);
        validateFreezeRequest(membership, membershipId, req);
        MembershipFreezeRequests saved = freezeRequestRepository.save(
                MembershipFreezeRequests.builder()
                        .membership(membership)
                        .freezeStart(req.freezeStart())
                        .freezeEnd(req.freezeEnd())
                        .status(FreezeRequestStatus.PENDING)
                        .createdAt(Instant.now())
                        .build());
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.freezeRequestSubmitted(
                        saved.getId(),
                        membership.getMembershipType().getName(),
                        req.freezeStart().toString(),
                        req.freezeEnd().toString()));
        return membershipFreezeMapper.toUserResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFreezeRequestResponse> getUserFreezeRequests(User currentUser, Long membershipId) {
        Membership membership = requireMembership(membershipId);
        assertOwner(currentUser, membership, ApplicationConstants.FREEZE_NOT_OWNER);
        return freezeRequestRepository.findAllByMembershipId(membershipId).stream()
                .map(membershipFreezeMapper::toUserResponse).toList();
    }

    @Override
    @Transactional
    public MembershipResponse renewMembership(User currentUser, Long membershipId, RenewMembershipRequest req) {
        Membership membership = requireMembership(membershipId);
        assertOwner(currentUser, membership, ApplicationConstants.RENEW_NOT_OWNER);
        assertRenewable(membership);
        applyRenewal(membership, req);
        membershipRepository.save(membership);
        paymentRepository.save(Payment.builder()
                .user(currentUser)
                .amount(membership.getMembershipType().getPrice())
                .paymentType(PaymentEntityType.MEMBERSHIP)
                .entityId(membership.getId())
                .status(PaymentStatus.SUCCESS)
                .build());
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminFreezeRequestResponse> listFreezeRequests(FreezeRequestStatus status, Pageable pageable) {
        Page<MembershipFreezeRequests> page = status == null
                ? freezeRequestRepository.findAllWithDetails(pageable)
                : freezeRequestRepository.findAllWithDetailsByStatus(status, pageable);
        return page.map(membershipFreezeMapper::toAdminResponse);
    }

    @Override
    @Transactional
    public AdminFreezeRequestResponse approveFreezeRequest(Long requestId, User reviewer) {
        MembershipFreezeRequests freezeRequest = requireFreezeRequest(requestId);
        assertPending(freezeRequest);
        applyFreeze(freezeRequest);
        finalizeReview(freezeRequest, reviewer, FreezeRequestStatus.APPROVED, null);
        var membership = freezeRequest.getMembership();
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.freezeRequestApproved(
                        freezeRequest.getId(),
                        membership.getUser().getId(),
                        membership.getMembershipType().getName(),
                        freezeRequest.getFreezeStart().toString(),
                        freezeRequest.getFreezeEnd().toString()));
        return membershipFreezeMapper.toAdminResponse(freezeRequest);
    }

    @Override
    @Transactional
    public AdminFreezeRequestResponse rejectFreezeRequest(Long requestId, User reviewer, RejectFreezeRequest body) {
        MembershipFreezeRequests freezeRequest = requireFreezeRequest(requestId);
        assertPending(freezeRequest);
        finalizeReview(freezeRequest, reviewer, FreezeRequestStatus.REJECTED, body != null ? body.reason() : null);
        var membership = freezeRequest.getMembership();
        String reason = body != null ? body.reason() : null;
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.freezeRequestRejected(
                        freezeRequest.getId(),
                        membership.getUser().getId(),
                        membership.getMembershipType().getName(),
                        reason == null ? "" : reason));
        return membershipFreezeMapper.toAdminResponse(freezeRequest);
    }

    private void validateFreezeRequest(Membership membership, Long membershipId, SubmitFreezeRequest req) {
        if (membership.getStatus() != MembershipStatus.ACTIVE)
            throw new ForbiddenOperationException(ApplicationConstants.FREEZE_INVALID_STATUS);
        if (req.freezeEnd().isBefore(req.freezeStart()))
            throw new ForbiddenOperationException(ApplicationConstants.FREEZE_END_BEFORE_START);
        if (ChronoUnit.DAYS.between(req.freezeStart(), req.freezeEnd()) + 1 > ApplicationConstants.MAX_FREEZE_DAYS)
            throw new ForbiddenOperationException(ApplicationConstants.FREEZE_MAX_DAYS_EXCEEDED);
        if (req.freezeStart().isBefore(LocalDate.now().plusDays(ApplicationConstants.MIN_NOTICE_DAYS)))
            throw new ForbiddenOperationException(ApplicationConstants.FREEZE_MIN_NOTICE_DAYS);
        if (freezeRequestRepository.existsByMembershipIdAndStatusInAndFreezeEndAfterAndFreezeStartBefore(
                membershipId,
                List.of(FreezeRequestStatus.PENDING, FreezeRequestStatus.APPROVED),
                req.freezeStart(), req.freezeEnd()))
            throw new MembershipRequestConflictException(ApplicationConstants.FREEZE_OVERLAP_EXISTS);
    }

    private void assertRenewable(Membership membership) {
        if (membership.getStatus() == MembershipStatus.EXPIRED || membership.getStatus() == MembershipStatus.CANCELLED)
            throw new ForbiddenOperationException(ApplicationConstants.RENEW_INVALID_STATUS);
    }

    private void assertPending(MembershipFreezeRequests freezeRequest) {
        if (freezeRequest.getStatus() != FreezeRequestStatus.PENDING)
            throw new MembershipRequestConflictException(ApplicationConstants.FREEZE_ALREADY_REVIEWED);
    }

    private void assertOwner(User user, Membership membership, String msg) {
        if (!membership.getUser().getId().equals(user.getId()))
            throw new ForbiddenOperationException(msg);
    }

    private void applyRenewal(Membership membership, RenewMembershipRequest req) {
        membership.setEndDate(membership.getEndDate().plusDays(membership.getMembershipType().getDurationDays()));
        if (req != null) {
            if (req.nutritionPlanId() != null) membership.setNutritionPlanId(req.nutritionPlanId());
            if (req.trainerId()       != null) membership.setTrainerId(req.trainerId());
            if (req.groupClassId()    != null) membership.setGroupClassId(req.groupClassId());
        }
    }

    private void applyFreeze(MembershipFreezeRequests freezeRequest) {
        long freezeDays = ChronoUnit.DAYS.between(freezeRequest.getFreezeStart(), freezeRequest.getFreezeEnd()) + 1;
        Membership membership = freezeRequest.getMembership();
        membership.setEndDate(membership.getEndDate().plusDays(freezeDays));
        membership.setStatus(MembershipStatus.FROZEN);
        membershipRepository.save(membership);
    }

    private void finalizeReview(MembershipFreezeRequests freezeRequest, User reviewer, FreezeRequestStatus status, String reason) {
        freezeRequest.setStatus(status);
        freezeRequest.setReviewedBy(reviewer);
        freezeRequest.setReviewedAt(Instant.now());
        freezeRequest.setRejectionReason(reason);
        freezeRequestRepository.save(freezeRequest);
    }

    private Membership requireMembership(Long id) {
        return membershipRepository.findByIdWithTypeAndUser(id)
                .orElseThrow(() -> new MembershipNotFoundException(ApplicationConstants.FREEZE_MEMBERSHIP_NOT_FOUND));
    }

    private MembershipFreezeRequests requireFreezeRequest(Long id) {
        return freezeRequestRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new MembershipRequestNotFoundException(ApplicationConstants.FREEZE_REQUEST_NOT_FOUND));
    }
}