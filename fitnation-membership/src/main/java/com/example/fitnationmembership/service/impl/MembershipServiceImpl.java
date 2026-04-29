package com.example.fitnationmembership.service.impl;

import com.example.fitnationbooking.repository.GroupClassRepository;
import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.request.RejectMembershipRequest;
import com.example.fitnationcommon.dto.request.SubmitMembershipRequest;
import com.example.fitnationcommon.dto.request.UpdateMembershipRequest;
import com.example.fitnationcommon.dto.response.AdminMembershipRecordResponse;
import com.example.fitnationcommon.dto.response.AdminMembershipRequestResponse;
import com.example.fitnationcommon.dto.response.AdminMembershipStatsResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationcommon.dto.response.UserMembershipRequestResponse;
import com.example.fitnationcommon.enums.MembershipRequestStatus;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationcommon.enums.PaymentEntityType;
import com.example.fitnationcommon.enums.PaymentStatus;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.GroupClassNotFoundException;
import com.example.fitnationcommon.exception.MembershipNotFoundException;
import com.example.fitnationcommon.exception.MembershipRequestConflictException;
import com.example.fitnationcommon.exception.MembershipRequestNotFoundException;
import com.example.fitnationcommon.exception.MembershipTypeNotFoundException;
import com.example.fitnationcommon.exception.NutritionPlanNotFoundException;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationmembership.mapper.MembershipMapper;
import com.example.fitnationmembership.mapper.MembershipTypeMapper;
import com.example.fitnationmembership.model.Membership;
import com.example.fitnationmembership.model.MembershipRequest;
import com.example.fitnationmembership.model.MembershipType;
import com.example.fitnationmembership.repository.MembershipRepository;
import com.example.fitnationmembership.repository.MembershipRequestRepository;
import com.example.fitnationmembership.repository.MembershipTypeRepository;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationprogress.factory.NotificationCommandFactory;
import com.example.fitnationprogress.service.NotificationCommandPublisher;
import com.example.fitnationuser.payment.Payment;
import com.example.fitnationuser.payment.PaymentRepository;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import com.fitnationnutrition.repository.NutritionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipTypeRepository membershipTypeRepository;
    private final MembershipRequestRepository membershipRequestRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final MembershipTypeMapper membershipTypeMapper;
    private final MembershipMapper membershipMapper;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final TrainerRepository trainerRepository;
    private final GroupClassRepository groupClassRepository;
    private final NotificationCommandPublisher notificationCommandPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<MembershipTypeResponse> getAllMembershipTypes() {
        return membershipTypeRepository.findAll().stream()
                .map(membershipTypeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MembershipTypeResponse createMembershipType(CreateMembershipTypeRequest request) {
        validateOptionalRefs(
                request.nutritionPlanId(),
                request.trainerId(),
                request.groupClassId());
        var saved = membershipTypeRepository.save(membershipTypeMapper.toEntity(request));
        return membershipTypeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public MembershipTypeResponse updateMembershipType(Long id, CreateMembershipTypeRequest request) {
        var entity = membershipTypeRepository.findById(id)
                .orElseThrow(() -> new MembershipTypeNotFoundException(ApplicationConstants.MEMBERSHIP_TYPE_NOT_FOUND));
        validateOptionalRefs(
                request.nutritionPlanId(),
                request.trainerId(),
                request.groupClassId());
        membershipTypeMapper.updateFromRequest(request, entity);
        return membershipTypeMapper.toResponse(membershipTypeRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteMembershipType(Long id) {
        if (!membershipTypeRepository.existsById(id)) {
            throw new MembershipTypeNotFoundException(ApplicationConstants.MEMBERSHIP_TYPE_NOT_FOUND);
        }
        long inUse = membershipRepository.countByMembershipType_Id(id);
        if (inUse > 0) {
            throw new ForbiddenOperationException(
                    String.format(ApplicationConstants.MEMBERSHIP_TYPE_IN_USE, inUse));
        }
        membershipTypeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MembershipResponse purchaseMembership(String email, PurchaseMembershipRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ApplicationConstants.MEMBERSHIP_USER_NOT_FOUND));

        var type = membershipTypeRepository.findById(request.membershipTypeId())
                .orElseThrow(() -> new MembershipTypeNotFoundException(ApplicationConstants.MEMBERSHIP_TYPE_NOT_FOUND));

        var bundle = resolvePurchaseBundleIds(request, type);
        validateOptionalRefs(bundle.nutritionPlanId(), bundle.trainerId(), bundle.groupClassId());

        var membership = createActiveMembershipWithPayment(
                user, type, LocalDate.now(), bundle.nutritionPlanId(), bundle.trainerId(), bundle.groupClassId());
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.membershipPurchaseConfirmed(
                        membership.getId(), user.getId(), type.getName()));
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MembershipResponse> getUserMemberships(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ApplicationConstants.MEMBERSHIP_USER_NOT_FOUND));

        return membershipRepository.findAllByUserId(user.getId(), pageable)
                .map(membershipMapper::toResponse);
    }

    @Override
    @Transactional
    public UserMembershipRequestResponse submitMembershipRequest(String userEmail, SubmitMembershipRequest request) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(ApplicationConstants.MEMBERSHIP_USER_NOT_FOUND));
        if (user.getRole() != UserRole.CLIENT) {
            throw new ForbiddenOperationException(ApplicationConstants.MEMBERSHIP_REQUEST_CLIENT_ONLY);
        }
        MembershipType type = membershipTypeRepository.findById(request.membershipTypeId())
                .orElseThrow(() -> new MembershipTypeNotFoundException(ApplicationConstants.MEMBERSHIP_TYPE_NOT_FOUND));
        Long uid = user.getId();
        Long tid = type.getId();
        if (membershipRequestRepository.existsByUser_IdAndMembershipType_IdAndStatus(
                uid, tid, MembershipRequestStatus.PENDING)) {
            throw new MembershipRequestConflictException(ApplicationConstants.MEMBERSHIP_REQUEST_PENDING_EXISTS);
        }
        if (membershipRepository.existsByUser_IdAndMembershipType_IdAndStatusAndEndDateGreaterThanEqual(
                uid, tid, MembershipStatus.ACTIVE, LocalDate.now())) {
            throw new MembershipRequestConflictException(ApplicationConstants.MEMBERSHIP_REQUEST_ACTIVE_EXISTS);
        }
        var saved = membershipRequestRepository.save(MembershipRequest.builder()
                .user(user)
                .membershipType(type)
                .status(MembershipRequestStatus.PENDING)
                .createdAt(Instant.now())
                .build());
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.membershipRequestSubmitted(saved.getId(), type.getName()));
        return toUserRequestResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserMembershipRequestResponse> getUserMembershipRequests(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(ApplicationConstants.MEMBERSHIP_USER_NOT_FOUND));
        return membershipRequestRepository.findAllByUserIdWithType(user.getId()).stream()
                .map(this::toUserRequestResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminMembershipRequestResponse> listMembershipRequestsForAdmin(
            MembershipRequestStatus statusFilter,
            Pageable pageable) {
        Page<MembershipRequest> page = Optional.ofNullable(statusFilter)
                .map(status -> membershipRequestRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable))
                .orElseGet(() -> membershipRequestRepository.findAllByOrderByCreatedAtDesc(pageable));
        return page.map(this::toAdminRequestResponse);
    }

    @Override
    @Transactional
    public AdminMembershipRequestResponse approveMembershipRequest(Long requestId, User reviewer) {
        var membershipRequest = requireMembershipRequest(requestId);
        assertPendingRequest(membershipRequest);
        var member = membershipRequest.getUser();
        var type = membershipRequest.getMembershipType();
        validateOptionalRefs(type.getNutritionPlanId(), type.getTrainerId(), type.getGroupClassId());
        createActiveMembershipWithPayment(
                member,
                type,
                LocalDate.now(),
                type.getNutritionPlanId(),
                type.getTrainerId(),
                type.getGroupClassId());
        finalizeRequestReview(membershipRequest, reviewer, MembershipRequestStatus.APPROVED, null);
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.membershipRequestApproved(
                        membershipRequest.getId(),
                        member.getId(),
                        type.getName()));
        return toAdminRequestResponse(membershipRequest);
    }

    @Override
    @Transactional
    public AdminMembershipRequestResponse rejectMembershipRequest(
            Long requestId,
            User reviewer,
            RejectMembershipRequest rejectBody) {
        var membershipRequest = requireMembershipRequest(requestId);
        assertPendingRequest(membershipRequest);
        var reason = Optional.ofNullable(rejectBody).map(RejectMembershipRequest::reason).orElse(null);
        finalizeRequestReview(membershipRequest, reviewer, MembershipRequestStatus.REJECTED, reason);
        var member = membershipRequest.getUser();
        var type = membershipRequest.getMembershipType();
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.membershipRequestRejected(
                        membershipRequest.getId(),
                        member.getId(),
                        type.getName(),
                        reason == null ? "" : reason));
        return toAdminRequestResponse(membershipRequest);
    }

    @Override
    @Transactional
    public MembershipResponse cancelMembership(Long membershipId, User currentUser) {
        Membership membership = membershipRepository.findByIdWithTypeAndUser(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(ApplicationConstants.MEMBERSHIP_NOT_FOUND));

        if (!canManageMembership(currentUser, membership)) {
            throw new ForbiddenOperationException(ApplicationConstants.CANNOT_CANCEL_MEMBERSHIP);
        }

        membership.markExpired();
        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional
    public MembershipResponse updateMembership(Long membershipId, UpdateMembershipRequest request, User currentUser) {
        Membership membership = membershipRepository.findByIdWithTypeAndUser(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(ApplicationConstants.MEMBERSHIP_NOT_FOUND));

        if (!canManageMembership(currentUser, membership)) {
            throw new ForbiddenOperationException(ApplicationConstants.CANNOT_UPDATE_MEMBERSHIP);
        }

        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException(ApplicationConstants.END_DATE_BEFORE_START_DATE);
        }

        var newType = membershipTypeRepository.findById(request.membershipTypeId())
                .orElseThrow(() -> new MembershipTypeNotFoundException(ApplicationConstants.MEMBERSHIP_TYPE_NOT_FOUND));

        validateOptionalRefs(
                request.nutritionPlanId(),
                request.trainerId(),
                request.groupClassId());

        membership.update(
                newType,
                request.startDate(),
                request.endDate(),
                request.status(),
                request.nutritionPlanId(),
                request.trainerId(),
                request.groupClassId()
        );
        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminMembershipRecordResponse> getAdminMemberships(Pageable pageable, String q, String status) {
        MembershipStatus membershipStatus = parseMembershipStatusFilter(status).orElse(null);

        return membershipRepository.findAllWithFilters(q, membershipStatus, pageable)
                .map(this::toAdminMembershipRecordResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminMembershipStatsResponse getAdminMembershipStats() {
        List<Membership> memberships = membershipRepository.findAllWithTypeAndUser();
        long total = memberships.size();
        Map<MembershipStatus, Long> byStatus = memberships.stream()
                .collect(Collectors.groupingBy(Membership::getStatus, Collectors.counting()));

        long active = byStatus.getOrDefault(MembershipStatus.ACTIVE, 0L);
        long cancelledOrExpired = byStatus.getOrDefault(MembershipStatus.CANCELLED, 0L)
                + byStatus.getOrDefault(MembershipStatus.EXPIRED, 0L);
        long pastDue = byStatus.getOrDefault(MembershipStatus.PAST_DUE, 0L);

        BigDecimal mrr = memberships.stream()
                .filter(m -> m.getStatus() == MembershipStatus.ACTIVE)
                .map(m -> m.getMembershipType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double churnRate = total > 0
                ? BigDecimal.valueOf(cancelledOrExpired * 100.0d / total)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue()
                : 0.0d;

        return new AdminMembershipStatsResponse(mrr, active, churnRate, pastDue);
    }

    private void validateOptionalRefs(Long nutritionPlanId, Long trainerId, Long groupClassId) {
        Stream.of(
                new RefCheck(nutritionPlanId, nutritionPlanRepository::existsById,
                        () -> new NutritionPlanNotFoundException(ApplicationConstants.MEMBERSHIP_NUTRITION_PLAN_NOT_FOUND)),
                new RefCheck(trainerId, trainerRepository::existsById,
                        () -> new TrainerNotFoundException(ApplicationConstants.TRAINER_NOT_FOUND)),
                new RefCheck(groupClassId, groupClassRepository::existsById,
                        () -> new GroupClassNotFoundException(ApplicationConstants.GROUP_CLASS_NOT_FOUND))
        ).forEach(RefCheck::validate);
    }

    private record RefCheck(Long id, Predicate<Long> exists, Supplier<RuntimeException> onMissing) {
        void validate() {
            if (id != null && !exists.test(id)) {
                throw onMissing.get();
            }
        }
    }

    private record PurchaseBundleIds(Long nutritionPlanId, Long trainerId, Long groupClassId) {}

    private static PurchaseBundleIds resolvePurchaseBundleIds(PurchaseMembershipRequest request, MembershipType type) {
        return new PurchaseBundleIds(
                Optional.ofNullable(request.nutritionPlanId()).orElse(type.getNutritionPlanId()),
                Optional.ofNullable(request.trainerId()).orElse(type.getTrainerId()),
                Optional.ofNullable(request.groupClassId()).orElse(type.getGroupClassId()));
    }

    private static Optional<MembershipStatus> parseMembershipStatusFilter(String raw) {
        return Optional.ofNullable(raw)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> MembershipStatus.valueOf(s.toUpperCase()));
    }

    private AdminMembershipRecordResponse toAdminMembershipRecordResponse(Membership membership) {
        var user = membership.getUser();
        var type = membership.getMembershipType();
        return new AdminMembershipRecordResponse(
                membership.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                type.getId(),
                type.getName(),
                type.getDurationDays(),
                type.getPrice(),
                membership.getStartDate(),
                membership.getEndDate(),
                membership.getStatus(),
                membership.getNutritionPlanId(),
                membership.getTrainerId(),
                membership.getGroupClassId());
    }

    private Membership createActiveMembershipWithPayment(
            User user,
            MembershipType type,
            LocalDate startDate,
            Long nutritionPlanId,
            Long trainerId,
            Long groupClassId) {
        LocalDate endDate = startDate.plusDays(type.getDurationDays());
        Membership membership = Membership.builder()
                .user(user)
                .membershipType(type)
                .startDate(startDate)
                .endDate(endDate)
                .status(MembershipStatus.ACTIVE)
                .nutritionPlanId(nutritionPlanId)
                .trainerId(trainerId)
                .groupClassId(groupClassId)
                .build();
        membershipRepository.save(membership);
        paymentRepository.save(Payment.builder()
                .user(user)
                .amount(type.getPrice())
                .paymentType(PaymentEntityType.MEMBERSHIP)
                .entityId(membership.getId())
                .status(PaymentStatus.SUCCESS)
                .build());
        return membership;
    }

    private void assertPendingRequest(MembershipRequest r) {
        switch (r.getStatus()) {
            case PENDING -> { }
            case APPROVED, REJECTED ->
                    throw new MembershipRequestConflictException(ApplicationConstants.MEMBERSHIP_REQUEST_ALREADY_REVIEWED);
        }
    }

    private void finalizeRequestReview(
            MembershipRequest request,
            User reviewer,
            MembershipRequestStatus status,
            String rejectionReason) {
        request.setStatus(status);
        request.setReviewedAt(Instant.now());
        request.setReviewedBy(reviewer);
        request.setRejectionReason(rejectionReason);
        membershipRequestRepository.save(request);
    }

    private MembershipRequest requireMembershipRequest(Long id) {
        return membershipRequestRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new MembershipRequestNotFoundException(ApplicationConstants.MEMBERSHIP_REQUEST_NOT_FOUND));
    }

    private UserMembershipRequestResponse toUserRequestResponse(MembershipRequest r) {
        MembershipType t = r.getMembershipType();
        return new UserMembershipRequestResponse(
                r.getId(),
                t.getId(),
                t.getName(),
                t.getDurationDays(),
                r.getStatus(),
                r.getCreatedAt(),
                r.getReviewedAt(),
                r.getRejectionReason());
    }

    private AdminMembershipRequestResponse toAdminRequestResponse(MembershipRequest r) {
        User u = r.getUser();
        MembershipType t = r.getMembershipType();
        Long reviewerId = r.getReviewedBy() != null ? r.getReviewedBy().getId() : null;
        return new AdminMembershipRequestResponse(
                r.getId(),
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                t.getId(),
                t.getName(),
                t.getDurationDays(),
                r.getStatus(),
                r.getCreatedAt(),
                r.getReviewedAt(),
                reviewerId,
                r.getRejectionReason());
    }

    private boolean canManageMembership(User currentUser, Membership membership) {
        return switch (currentUser.getRole()) {
            case ADMIN -> true;
            case CLIENT, TRAINER -> membership.getUser().getId().equals(currentUser.getId());
        };
    }
}