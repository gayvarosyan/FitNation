package com.example.fitnationmembership.service.impl;

import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.request.SubmitMembershipRequest;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationmembership.mapper.MembershipMapper;
import com.example.fitnationmembership.mapper.MembershipTypeMapper;
import com.example.fitnationmembership.model.Membership;
import com.example.fitnationmembership.repository.*;
import com.example.fitnationbooking.repository.GroupClassRepository;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationprogress.service.NotificationCommandPublisher;
import com.example.fitnationuser.payment.PaymentRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import com.example.fitnationuser.validation.SoftDeleteValidationService;
import com.fitnationnutrition.repository.NutritionPlanRepository;
import com.example.fitnationcommon.rbac.RbacPolicyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipServiceImplTest {

    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private MembershipTypeRepository membershipTypeRepository;
    @Mock
    private MembershipRequestRepository membershipRequestRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MembershipTypeMapper membershipTypeMapper;
    @Mock
    private MembershipMapper membershipMapper;
    @Mock
    private NutritionPlanRepository nutritionPlanRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private GroupClassRepository groupClassRepository;
    @Mock
    private NotificationCommandPublisher notificationCommandPublisher;
    @Mock
    private SoftDeleteValidationService softDeleteValidationService;
    @Mock
    private RbacPolicyService rbacPolicyService;

    @InjectMocks
    private MembershipServiceImpl membershipService;

    @Test
    void purchaseMembership_throwsWhenUserMissing() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                membershipService.purchaseMembership(
                        "missing@test.com",
                        new PurchaseMembershipRequest(1L, null, null, null)));
    }

    @Test
    void deleteMembershipType_throwsWhenPlanStillReferenced() {
        when(membershipTypeRepository.existsById(10L)).thenReturn(true);
        when(membershipRepository.countByMembershipType_Id(10L)).thenReturn(3L);

        ForbiddenOperationException ex = assertThrows(ForbiddenOperationException.class, () ->
                membershipService.deleteMembershipType(10L));

        assertTrue(ex.getMessage().contains("3 subscription record(s)"));
    }

    @Test
    void submitMembershipRequest_throwsWhenCallerNotClient() {
        User trainer = new User();
        trainer.setEmail("t@test.com");
        trainer.setRole(UserRole.TRAINER);

        when(userRepository.findByEmail("t@test.com")).thenReturn(Optional.of(trainer));

        org.mockito.Mockito.doNothing().when(softDeleteValidationService).validateUserForMembership(trainer);

        doThrow(new ForbiddenOperationException("not allowed"))
                .when(rbacPolicyService)
                .requireClientOrAdmin(any());

        assertThrows(ForbiddenOperationException.class, () ->
                membershipService.submitMembershipRequest(
                        "t@test.com",
                        new SubmitMembershipRequest(1L)));
    }

    @Test
    void cancelMembership_throwsWhenClientDoesNotOwnMembership() {
        User owner = new User();
        owner.setId(2L);

        User current = new User();
        current.setId(1L);
        current.setRole(UserRole.CLIENT);

        Membership membership = Membership.builder()
                .user(owner)
                .status(MembershipStatus.ACTIVE)
                .build();

        when(membershipRepository.findByIdWithTypeAndUser(99L))
                .thenReturn(Optional.of(membership));

        doThrow(new ForbiddenOperationException("not owner"))
                .when(rbacPolicyService)
                .requireOwnershipOrAdmin(any(), any(), any());

        assertThrows(ForbiddenOperationException.class, () ->
                membershipService.cancelMembership(99L, current));
    }
}