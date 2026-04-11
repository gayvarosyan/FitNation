package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.response.AdminFreezeRequestResponse;
import com.example.fitnationcommon.dto.request.RejectFreezeRequest;
import com.example.fitnationcommon.dto.request.RenewMembershipRequest;
import com.example.fitnationcommon.dto.request.SubmitFreezeRequest;
import com.example.fitnationcommon.dto.response.UserFreezeRequestResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.enums.FreezeRequestStatus;
import com.example.fitnationmembership.service.MembershipFreezeService;
import com.example.fitnationuser.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MembershipFreezeEndpoint {

    private final MembershipFreezeService freezeService;

    @PostMapping("/api/users/memberships/{membershipId}/freeze-requests")
    public ResponseEntity<UserFreezeRequestResponse> submitFreezeRequest(
            @AuthenticationPrincipal User user,
            @PathVariable Long membershipId,
            @Valid @RequestBody SubmitFreezeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(freezeService.submitFreezeRequest(user, membershipId, request));
    }

    @GetMapping("/api/users/memberships/{membershipId}/freeze-requests")
    public ResponseEntity<List<UserFreezeRequestResponse>> getUserFreezeRequests(
            @AuthenticationPrincipal User user,
            @PathVariable Long membershipId) {
        return ResponseEntity.ok(freezeService.getUserFreezeRequests(user, membershipId));
    }

    @PostMapping("/api/users/memberships/{membershipId}/renew")
    public ResponseEntity<MembershipResponse> renewMembership(
            @AuthenticationPrincipal User user,
            @PathVariable Long membershipId,
            @RequestBody(required = false) RenewMembershipRequest request) {
        return ResponseEntity.ok(freezeService.renewMembership(user, membershipId, request));
    }

    @GetMapping("/api/admin/membership-freeze-requests")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Page<AdminFreezeRequestResponse>> listFreezeRequests(
            @RequestParam(required = false) FreezeRequestStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(freezeService.listFreezeRequests(status, pageable));
    }

    @PostMapping("/api/admin/membership-freeze-requests/{requestId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<AdminFreezeRequestResponse> approveFreezeRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(freezeService.approveFreezeRequest(requestId, admin));
    }

    @PostMapping("/api/admin/membership-freeze-requests/{requestId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<AdminFreezeRequestResponse> rejectFreezeRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User admin,
            @RequestBody(required = false) RejectFreezeRequest body) {
        return ResponseEntity.ok(freezeService.rejectFreezeRequest(requestId, admin, body));
    }
}
