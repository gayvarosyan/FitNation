package com.example.fitnationmembership.service.impl;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationcommon.enums.PaymentEntityType;
import com.example.fitnationcommon.enums.PaymentStatus;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.MembershipNotFoundException;
import com.example.fitnationcommon.exception.MembershipTypeNotFoundException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationmembership.mapper.MembershipMapper;
import com.example.fitnationmembership.mapper.MembershipTypeMapper;
import com.example.fitnationmembership.model.Membership;
import com.example.fitnationmembership.repository.MembershipRepository;
import com.example.fitnationmembership.repository.MembershipTypeRepository;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationuser.payment.Payment;
import com.example.fitnationuser.payment.PaymentRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        var saved = membershipTypeRepository.save(membershipTypeMapper.toEntity(request));
        return membershipTypeMapper.toResponse(saved);
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

        Membership membership = Membership.builder()
                .user(user)
                .membershipType(type)
                .startDate(startDate)
                .endDate(endDate)
                .status(MembershipStatus.ACTIVE)
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

    private boolean canManageMembership(User currentUser, Membership membership) {
        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.SUPER_ADMIN) {
            return true;
        }
        return membership.getUser().getId().equals(currentUser.getId());
    }
}
