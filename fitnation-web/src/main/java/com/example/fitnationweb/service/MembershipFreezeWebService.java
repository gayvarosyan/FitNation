package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.request.RejectFreezeRequest;
import com.example.fitnationcommon.dto.request.RenewMembershipRequest;
import com.example.fitnationcommon.dto.request.SubmitFreezeRequest;
import com.example.fitnationcommon.dto.response.AdminFreezeRequestResponse;
import com.example.fitnationcommon.dto.response.UserFreezeRequestResponse;
import com.example.fitnationcommon.enums.FreezeRequestStatus;
import com.example.fitnationmembership.service.MembershipFreezeService;
import com.example.fitnationuser.user.User;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipFreezeWebService {

    private static final int PAGE_SIZE = 100;
    private static final String ADMIN_FREEZE_REQUESTS_PATH = "/admin/membership-freeze-requests";

    private final MembershipFreezeService membershipFreezeService;

    public List<UserFreezeRequestResponse> getUserFreezeRequests(User user, Long membershipId) {
        return membershipFreezeService.getUserFreezeRequests(user, membershipId);
    }

    public void submitFreezeRequest(User user, Long membershipId, LocalDate freezeStart, LocalDate freezeEnd) {
        membershipFreezeService.submitFreezeRequest(user, membershipId, new SubmitFreezeRequest(freezeStart, freezeEnd));
    }

    public void renewMembership(User user, Long membershipId, Long nutritionPlanId, Long trainerId, Long groupClassId) {
        membershipFreezeService.renewMembership(
                user, membershipId, new RenewMembershipRequest(nutritionPlanId, trainerId, groupClassId));
    }

    public Page<AdminFreezeRequestResponse> listFreezeRequests(FreezeRequestStatus status) {
        return membershipFreezeService.listFreezeRequests(status, freezeRequestPageable());
    }

    public void approveFreezeRequest(Long requestId, User reviewer) {
        membershipFreezeService.approveFreezeRequest(requestId, reviewer);
    }

    public void rejectFreezeRequest(Long requestId, User reviewer, String reason) {
        membershipFreezeService.rejectFreezeRequest(requestId, reviewer, new RejectFreezeRequest(reason));
    }

    public MvcRedirect submitFreezeRequestForMvc(User user, Long membershipId, LocalDate freezeStart, LocalDate freezeEnd) {
        String path = freezePath(membershipId);
        try {
            submitFreezeRequest(user, membershipId, freezeStart, freezeEnd);
            return MvcRedirect.to(path, "Freeze request submitted.");
        } catch (Exception e) {
            return MvcRedirect.failure(path, e.getMessage());
        }
    }

    public MvcRedirect renewMembershipForMvc(
            User user,
            Long membershipId,
            Long nutritionPlanId,
            Long trainerId,
            Long groupClassId) {
        String path = freezePath(membershipId);
        try {
            renewMembership(user, membershipId, nutritionPlanId, trainerId, groupClassId);
            return MvcRedirect.to(path, "Membership renewed.");
        } catch (Exception e) {
            return MvcRedirect.failure(path, e.getMessage());
        }
    }

    public MvcRedirect approveFreezeRequestForMvc(Long requestId, User reviewer) {
        try {
            approveFreezeRequest(requestId, reviewer);
            return MvcRedirect.to(ADMIN_FREEZE_REQUESTS_PATH, "Freeze request approved.");
        } catch (Exception e) {
            return MvcRedirect.failure(ADMIN_FREEZE_REQUESTS_PATH, e.getMessage());
        }
    }

    public MvcRedirect rejectFreezeRequestForMvc(Long requestId, User reviewer, String reason) {
        try {
            rejectFreezeRequest(requestId, reviewer, reason);
            return MvcRedirect.to(ADMIN_FREEZE_REQUESTS_PATH, "Freeze request rejected.");
        } catch (Exception e) {
            return MvcRedirect.failure(ADMIN_FREEZE_REQUESTS_PATH, e.getMessage());
        }
    }

    private static PageRequest freezeRequestPageable() {
        return PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private static String freezePath(Long membershipId) {
        return "/portal/memberships/" + membershipId + "/freeze";
    }
}
