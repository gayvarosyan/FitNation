package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.RejectMembershipRequest;
import com.example.fitnationcommon.dto.response.AdminMembershipRequestResponse;
import com.example.fitnationcommon.enums.MembershipRequestStatus;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class SuperAdminMembershipController {

    private final MembershipService membershipService;

    @GetMapping("/membership-requests")
    public Page<AdminMembershipRequestResponse> listMembershipRequests(
            @RequestParam(required = false) MembershipRequestStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return membershipService.listMembershipRequestsForSuperAdmin(status, pageable);
    }

    @PutMapping("/membership-requests/{id}/approve")
    public AdminMembershipRequestResponse approveMembershipRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User reviewer) {
        return membershipService.approveMembershipRequest(id, reviewer);
    }

    @PutMapping("/membership-requests/{id}/reject")
    public AdminMembershipRequestResponse rejectMembershipRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User reviewer,
            @RequestBody(required = false) RejectMembershipRequest body) {
        RejectMembershipRequest payload = body != null ? body : new RejectMembershipRequest(null);
        return membershipService.rejectMembershipRequest(id, reviewer, payload);
    }
}
