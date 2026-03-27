package com.example.fitnationmembership.service.impl;

import com.example.fitnationbooking.repository.GroupClassRepository;
import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.request.UpdateMembershipRequest;
import com.example.fitnationcommon.dto.response.AdminMembershipRecordResponse;
import com.example.fitnationcommon.dto.response.AdminMembershipStatsResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationcommon.enums.PaymentEntityType;
import com.example.fitnationcommon.enums.PaymentStatus;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.GroupClassNotFoundException;
import com.example.fitnationcommon.exception.MembershipNotFoundException;
import com.example.fitnationcommon.exception.MembershipTypeNotFoundException;
import com.example.fitnationcommon.exception.NutritionPlanNotFoundException;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationmembership.mapper.MembershipMapper;
import com.example.fitnationmembership.mapper.MembershipTypeMapper;
import com.example.fitnationmembership.model.Membership;
import com.example.fitnationmembership.repository.MembershipRepository;
import com.example.fitnationmembership.repository.MembershipTypeRepository;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationuser.payment.Payment;
import com.example.fitnationuser.payment.PaymentRepository;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import com.fitnationnutrition.repository.NutritionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipTypeRepository membershipTypeRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final MembershipTypeMapper membershipTypeMapper;
    private final MembershipMapper membershipMapper;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final TrainerRepository trainerRepository;
    private final GroupClassRepository groupClassRepository;

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
                .orElseThrow(() -> new MembershipTypeNotFoundException("Membership type not found"));
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
            throw new MembershipTypeNotFoundException("Membership type not found");
        }
        long inUse = membershipRepository.countByMembershipType_Id(id);
        if (inUse > 0) {
            throw new ForbiddenOperationException(
                    "Cannot delete this plan: " + inUse + " subscription record(s) still reference it.");
        }
        membershipTypeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MembershipResponse purchaseMembership(String email, PurchaseMembershipRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var type = membershipTypeRepository.findById(request.membershipTypeId())
                .orElseThrow(() -> new MembershipTypeNotFoundException("Membership type not found"));

        var startDate = LocalDate.now();
        var endDate = startDate.plusDays(type.getDurationDays());

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

        Payment payment = Payment.builder()
                .user(user)
                .amount(type.getPrice())
                .paymentType(PaymentEntityType.MEMBERSHIP)
                .entityId(membership.getId())
                .status(PaymentStatus.SUCCESS)
                .build();
        paymentRepository.save(payment);

        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipResponse> getUserMemberships(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return membershipRepository.findAllByUserIdWithType(user.getId()).stream()
                .map(membershipMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MembershipResponse cancelMembership(Long membershipId, User currentUser) {
        Membership membership = membershipRepository.findByIdWithTypeAndUser(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException("Membership not found"));

        if (!canManageMembership(currentUser, membership)) {
            throw new ForbiddenOperationException("You cannot cancel this membership");
        }

        membership.markExpired();
        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional
    public MembershipResponse updateMembership(Long membershipId, UpdateMembershipRequest request, User currentUser) {
        Membership membership = membershipRepository.findByIdWithTypeAndUser(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException("Membership not found"));

        if (!canManageMembership(currentUser, membership)) {
            throw new ForbiddenOperationException("You cannot update this membership");
        }

        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }

        var newType = membershipTypeRepository.findById(request.membershipTypeId())
                .orElseThrow(() -> new MembershipTypeNotFoundException("Membership type not found"));

        validateOptionalRefs(
                request.nutritionPlanId(),
                request.trainerId(),
                request.groupClassId());

        membership.setMembershipType(newType);
        membership.setStartDate(request.startDate());
        membership.setEndDate(request.endDate());
        membership.setStatus(request.status());
        membership.setNutritionPlanId(request.nutritionPlanId());
        membership.setTrainerId(request.trainerId());
        membership.setGroupClassId(request.groupClassId());
        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    private void validateOptionalRefs(Long nutritionPlanId, Long trainerId, Long groupClassId) {
        if (nutritionPlanId != null && !nutritionPlanRepository.existsById(nutritionPlanId)) {
            throw new NutritionPlanNotFoundException("Nutrition plan not found");
        }
        if (trainerId != null && !trainerRepository.existsById(trainerId)) {
            throw new TrainerNotFoundException("Trainer not found");
        }
        if (groupClassId != null && !groupClassRepository.existsById(groupClassId)) {
            throw new GroupClassNotFoundException("Group class not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminMembershipRecordResponse> getAdminMemberships() {
        return membershipRepository.findAllWithTypeAndUser().stream()
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
                ))
                .toList();
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

    private boolean canManageMembership(User currentUser, Membership membership) {
        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.SUPER_ADMIN) {
            return true;
        }
        return membership.getUser().getId().equals(currentUser.getId());
    }
}
