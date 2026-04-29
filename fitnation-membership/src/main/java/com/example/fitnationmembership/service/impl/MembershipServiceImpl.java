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
import com.example.fitnationcommon.rbac.RbacPolicyService;
import com.example.fitnationmembership.mapper.MembershipMapper;
import com.example.fitnationmembership.mapper.MembershipTypeMapper;
import com.example.fitnationmembership.model.Membership;
import com.example.fitnationmembership.model.MembershipRequest;
import com.example.fitnationmembership.model.MembershipType;
import com.example.fitnationmembership.repository.MembershipRepository;
import com.example.fitnationmembership.repository.MembershipRequestRepository;
import com.example.fitnationmembership.repository.MembershipTypeRepository;
import com.example.fitnationmembership.service.MembershipService;
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
    private final RbacPolicyService rbacPolicyService;

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

        Long nutritionPlanId = request.nutritionPlanId() != null
                ? request.nutritionPlanId()
                : type.getNutritionPlanId();
        Long trainerId = request.trainerId() != null
                ? request.trainerId()
                : type.getTrainerId();
        Long groupClassId = request.groupClassId() != null
                ? request.groupClassId()
                : type.getGroupClassId();
        validateOptionalRefs(nutritionPlanId, trainerId, groupClassId);

        var membership = createActiveMembershipWithPayment(
                user, type, LocalDate.now(), nutritionPlanId, trainerId, groupClassId);
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

        rbacPolicyService.requireClientOrAdmin(user.getRole());

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
        Page<MembershipRequest> page = statusFilter == null
                ? membershipRequestRepository.findAllByOrderByCreatedAtDesc(pageable)
                : membershipRequestRepository.findAllByStatusOrderByCreatedAtDesc(statusFilter, pageable);
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
        var reason = rejectBody != null ? rejectBody.reason() : null;
        finalizeRequestReview(membershipRequest, reviewer, MembershipRequestStatus.REJECTED, reason);
        return toAdminRequestResponse(membershipRequest);
    }

    @Override
    @Transactional
    public MembershipResponse cancelMembership(Long membershipId, User currentUser) {
        Membership membership = membershipRepository.findByIdWithTypeAndUser(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(ApplicationConstants.MEMBERSHIP_NOT_FOUND));

        rbacPolicyService.requireOwnershipOrAdmin(
                currentUser.getId(),
                membership.getUser().getId(),
                currentUser.getRole());

        membership.markExpired();
        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional
    public MembershipResponse updateMembership(Long membershipId, UpdateMembershipRequest request, User currentUser) {
        Membership membership = membershipRepository.findByIdWithTypeAndUser(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(ApplicationConstants.MEMBERSHIP_NOT_FOUND));

        rbacPolicyService.requireOwnershipOrAdmin(
                currentUser.getId(),
                membership.getUser().getId(),
                currentUser.getRole());

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

    private void validateOptionalRefs(Long nutritionPlanId, Long trainerId, Long groupClassId) {
        if (nutritionPlanId != null && !nutritionPlanRepository.existsById(nutritionPlanId)) {
            throw new NutritionPlanNotFoundException(ApplicationConstants.MEMBERSHIP_NUTRITION_PLAN_NOT_FOUND);
        }
        if (trainerId != null && !trainerRepository.existsById(trainerId)) {
            throw new TrainerNotFoundException(ApplicationConstants.TRAINER_NOT_FOUND);
        }
        if (groupClassId != null && !groupClassRepository.existsById(groupClassId)) {
            throw new GroupClassNotFoundException(ApplicationConstants.GROUP_CLASS_NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminMembershipRecordResponse> getAdminMemberships(Pageable pageable, String q, String status) {
        MembershipStatus membershipStatus = null;
        if (status != null && !status.isBlank()) {
            membershipStatus = MembershipStatus.valueOf(status.toUpperCase());
        }

        return membershipRepository.findAllWithFilters(q, membershipStatus, pageable)
                .map(membership -> new AdminMembershipRecordResponse(
                        membership.getId(),
                        membership.getUser().getId(),
                        membership.getUser().getFirstName(),
                        membership.getUser().getLastName(),
                        membership.getUser().getEmail(),
                        membership.getMembershipType().getId(),
                        membership.getMembershipType().getName(),
                        membership.getMembershipType().getDurationDays(),
                        membership.getMembershipType().getPrice(),
                        membership.getStartDate(),
                        membership.getEndDate(),
                        membership.getStatus(),
                        membership.getNutritionPlanId(),
                        membership.getTrainerId(),
                        membership.getGroupClassId()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public AdminMembershipStatsResponse getAdminMembershipStats() {
        List<Membership> memberships = membershipRepository.findAllWithTypeAndUser();
        long total = memberships.size();
        long active = memberships.stream()
                .filter(membership -> membership.getStatus() == MembershipStatus.ACTIVE)
                .count();
        long cancelledOrExpired = memberships.stream()
                .filter(membership -> membership.getStatus() == MembershipStatus.CANCELLED
                        || membership.getStatus() == MembershipStatus.EXPIRED)
                .count();
        long pastDue = memberships.stream()
                .filter(membership -> membership.getStatus() == MembershipStatus.PAST_DUE)
                .count();
        BigDecimal mrr = memberships.stream()
                .filter(membership -> membership.getStatus() == MembershipStatus.ACTIVE)
                .map(membership -> membership.getMembershipType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double churnRate = 0.0d;
        if (total > 0) {
            churnRate = BigDecimal.valueOf(cancelledOrExpired * 100.0d / total)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new AdminMembershipStatsResponse(mrr, active, churnRate, pastDue);
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
        if (r.getStatus() != MembershipRequestStatus.PENDING) {
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
}