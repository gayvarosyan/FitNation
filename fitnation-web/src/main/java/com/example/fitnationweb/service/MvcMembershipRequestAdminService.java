package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.request.RejectMembershipRequest;
import com.example.fitnationcommon.dto.response.AdminMembershipRequestResponse;
import com.example.fitnationcommon.enums.MembershipRequestStatus;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationweb.support.MvcRedirect;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MvcMembershipRequestAdminService {

    private final MembershipService membershipService;
    private final MvcValidationService validationService;

    public Page<AdminMembershipRequestResponse> list(String status, int page, int size) {
        MembershipRequestStatus filter = parseStatus(status);
        Pageable pageable = PageRequest.of(validationService.normalizePage(page), validationService.normalizeSize(size));
        return membershipService.listMembershipRequestsForAdmin(filter, pageable);
    }

    public MvcRedirect approve(String pagePath, Long requestId, User actor) {
        try {
            membershipService.approveMembershipRequest(requestId, actor);
            return MvcRedirect.to(pagePath, "Request approved successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(pagePath, e.getMessage());
        }
    }

    public MvcRedirect reject(String pagePath, Long requestId, User actor, String reason) {
        try {
            membershipService.rejectMembershipRequest(requestId, actor, new RejectMembershipRequest(reason));
            return MvcRedirect.to(pagePath, "Request rejected.");
        } catch (Exception e) {
            return MvcRedirect.failure(pagePath, e.getMessage());
        }
    }

    private static MembershipRequestStatus parseStatus(String raw) {
        if (raw == null || raw.isBlank() || "ALL".equalsIgnoreCase(raw.trim())) {
            return null;
        }
        try {
            return MembershipRequestStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

