package com.example.fitnationmembership.service;

import com.example.fitnationcommon.dto.response.AdminFreezeRequestResponse;
import com.example.fitnationcommon.dto.request.RejectFreezeRequest;
import com.example.fitnationcommon.dto.request.RenewMembershipRequest;
import com.example.fitnationcommon.dto.request.SubmitFreezeRequest;
import com.example.fitnationcommon.dto.response.UserFreezeRequestResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.enums.FreezeRequestStatus;
import com.example.fitnationuser.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MembershipFreezeService {
    UserFreezeRequestResponse submitFreezeRequest(User currentUser, Long membershipId, SubmitFreezeRequest req);

    List<UserFreezeRequestResponse> getUserFreezeRequests(User currentUser, Long membershipId);

    MembershipResponse renewMembership(User currentUser, Long membershipId, RenewMembershipRequest req);

    Page<AdminFreezeRequestResponse> listFreezeRequests(FreezeRequestStatus status, Pageable pageable);

    AdminFreezeRequestResponse approveFreezeRequest(Long requestId, User reviewer);

    AdminFreezeRequestResponse rejectFreezeRequest(Long requestId, User reviewer, RejectFreezeRequest body);
}

