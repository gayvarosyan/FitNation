package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.RejectMembershipRequest;
import com.example.fitnationcommon.dto.response.AdminMembershipRequestResponse;
import com.example.fitnationcommon.enums.MembershipRequestStatus;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationuser.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/admin/membership-requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin membership requests", description = "Review user-submitted membership requests (ADMIN)")
public class AdminMembershipRequestController {

    private final MembershipService membershipService;

    @Operation(summary = "List membership requests (paged)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Page returned"))
    @GetMapping
    public Page<AdminMembershipRequestResponse> listMembershipRequests(
            @RequestParam(required = false) MembershipRequestStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return membershipService.listMembershipRequestsForAdmin(status, pageable);
    }

    @Operation(summary = "Approve membership request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request approved"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PutMapping("/{id}/approve")
    public AdminMembershipRequestResponse approveMembershipRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User reviewer) {
        return membershipService.approveMembershipRequest(id, reviewer);
    }

    @Operation(summary = "Reject membership request", description = "Optional rejection body.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request rejected"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PutMapping("/{id}/reject")
    public AdminMembershipRequestResponse rejectMembershipRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User reviewer,
            @RequestBody(required = false) RejectMembershipRequest body) {
        RejectMembershipRequest payload = body != null ? body : new RejectMembershipRequest(null);
        return membershipService.rejectMembershipRequest(id, reviewer, payload);
    }
}
