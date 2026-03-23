package com.example.fitnationuser.membership;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationuser.payment.Payment;
import com.example.fitnationuser.payment.PaymentRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipTypeRepository membershipTypeRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public List<MembershipType> getAllMembershipTypes() {
        return membershipTypeRepository.findAll();
    }

    public MembershipType createMembershipType(CreateMembershipTypeRequest request) {
        MembershipType type = new MembershipType();
        type.setName(request.name());
        type.setDurationDays(request.durationDays());
        type.setPrice(request.price());
        type.setDescription(request.description());
        return membershipTypeRepository.save(type);
    }

    public MembershipResponse purchaseMembership(String email, PurchaseMembershipRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MembershipType type = membershipTypeRepository.findById(request.membershipTypeId())
                .orElseThrow(() -> new RuntimeException("Membership type not found"));

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(type.getDurationDays());

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setMembershipType(type);
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setStatus(MembershipStatus.ACTIVE);
        membershipRepository.save(membership);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(type.getPrice());
        payment.setPaymentType("MEMBERSHIP");
        payment.setEntityId(membership.getId());
        payment.setStatus("COMPLETED");
        paymentRepository.save(payment);

        return toResponse(membership);
    }

    public List<MembershipResponse> getUserMemberships(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return membershipRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MembershipResponse cancelMembership(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        membership.setStatus(MembershipStatus.EXPIRED);
        membershipRepository.save(membership);

        return toResponse(membership);
    }

    private MembershipResponse toResponse(Membership m) {
        return new MembershipResponse(
                m.getId(),
                m.getMembershipType().getName(),
                m.getStartDate(),
                m.getEndDate(),
                m.getStatus()
        );
    }
}
