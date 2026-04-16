package com.example.fitnationmembership.service.impl;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.SubmitFreezeRequest;
import com.example.fitnationcommon.enums.FreezeRequestStatus;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.MembershipRequestConflictException;
import com.example.fitnationmembership.mapper.MembershipFreezeMapper;
import com.example.fitnationmembership.mapper.MembershipMapper;
import com.example.fitnationmembership.model.Membership;
import com.example.fitnationmembership.model.MembershipType;
import com.example.fitnationmembership.repository.MembershipFreezeRequestRepository;
import com.example.fitnationmembership.repository.MembershipRepository;
import com.example.fitnationuser.payment.PaymentRepository;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipFreezeServiceImplTest {

    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private MembershipFreezeRequestRepository freezeRequestRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private MembershipMapper membershipMapper;
    @Mock
    private MembershipFreezeMapper membershipFreezeMapper;

    @InjectMocks
    private MembershipFreezeServiceImpl freezeService;

    @Test
    void submitFreezeRequest_throwsWhenNotOwner() {
        User owner = User.builder().id(1L).email("o@test.com").build();
        User other = User.builder().id(2L).email("x@test.com").build();
        MembershipType type = MembershipType.builder()
                .name("Basic")
                .durationDays(30)
                .price(BigDecimal.TEN)
                .build();
        Membership membership = Membership.builder()
                .user(owner)
                .membershipType(type)
                .status(MembershipStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .build();
        membership.setId(50L);

        when(membershipRepository.findByIdWithTypeAndUser(50L)).thenReturn(Optional.of(membership));

        SubmitFreezeRequest req = new SubmitFreezeRequest(
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5));

        assertThrows(ForbiddenOperationException.class, () ->
                freezeService.submitFreezeRequest(other, 50L, req));
    }

    @Test
    void submitFreezeRequest_throwsWhenMembershipNotActive() {
        User owner = User.builder().id(1L).build();
        MembershipType type = MembershipType.builder()
                .name("Basic")
                .durationDays(30)
                .price(BigDecimal.TEN)
                .build();
        Membership membership = Membership.builder()
                .user(owner)
                .membershipType(type)
                .status(MembershipStatus.EXPIRED)
                .startDate(LocalDate.now().minusMonths(2))
                .endDate(LocalDate.now().minusMonths(1))
                .build();
        membership.setId(51L);

        when(membershipRepository.findByIdWithTypeAndUser(51L)).thenReturn(Optional.of(membership));

        SubmitFreezeRequest req = new SubmitFreezeRequest(
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5));

        ForbiddenOperationException ex = assertThrows(ForbiddenOperationException.class, () ->
                freezeService.submitFreezeRequest(owner, 51L, req));
        org.junit.jupiter.api.Assertions.assertEquals(
                ApplicationConstants.FREEZE_INVALID_STATUS, ex.getMessage());
    }

    @Test
    void submitFreezeRequest_throwsWhenOverlappingRequestExists() {
        User owner = User.builder().id(1L).build();
        MembershipType type = MembershipType.builder()
                .name("Basic")
                .durationDays(30)
                .price(BigDecimal.TEN)
                .build();
        Membership membership = Membership.builder()
                .user(owner)
                .membershipType(type)
                .status(MembershipStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(2))
                .build();
        membership.setId(52L);

        when(membershipRepository.findByIdWithTypeAndUser(52L)).thenReturn(Optional.of(membership));
        when(freezeRequestRepository.existsByMembershipIdAndStatusInAndFreezeEndAfterAndFreezeStartBefore(
                eq(52L),
                eq(List.of(FreezeRequestStatus.PENDING, FreezeRequestStatus.APPROVED)),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(true);

        SubmitFreezeRequest req = new SubmitFreezeRequest(
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5));

        assertThrows(MembershipRequestConflictException.class, () ->
                freezeService.submitFreezeRequest(owner, 52L, req));
    }
}
