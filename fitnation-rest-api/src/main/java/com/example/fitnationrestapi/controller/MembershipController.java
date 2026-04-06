package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.request.SubmitMembershipRequest;
import com.example.fitnationcommon.dto.request.UpdateMembershipRequest;
import com.example.fitnationcommon.dto.response.AdminMembershipRecordResponse;
import com.example.fitnationcommon.dto.response.AdminMembershipStatsResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationcommon.dto.response.UserMembershipRequestResponse;
import com.example.fitnationmembership.service.MembershipService;
import com.example.fitnationuser.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/api/membership-types")
    public ResponseEntity<List<MembershipTypeResponse>> getMembershipTypes() {
        return ResponseEntity.ok(membershipService.getAllMembershipTypes());
    }

    @PostMapping("/api/admin/membership-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MembershipTypeResponse> createMembershipType(
            @RequestBody CreateMembershipTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.createMembershipType(request));
    }

    @PutMapping("/api/admin/membership-types/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MembershipTypeResponse> updateMembershipType(
            @PathVariable Long id,
            @RequestBody CreateMembershipTypeRequest request) {
        return ResponseEntity.ok(membershipService.updateMembershipType(id, request));
    }

    @DeleteMapping("/api/admin/membership-types/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteMembershipType(@PathVariable Long id) {
        membershipService.deleteMembershipType(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/users/memberships")
    public ResponseEntity<MembershipResponse> purchaseMembership(
            @AuthenticationPrincipal User user,
            @RequestBody PurchaseMembershipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.purchaseMembership(user.getEmail(), request));
    }

    @GetMapping("/api/users/memberships")
    public ResponseEntity<List<MembershipResponse>> getUserMemberships(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                membershipService.getUserMemberships(user.getEmail()));
    }

    @PostMapping("/api/users/membership-requests")
    public ResponseEntity<UserMembershipRequestResponse> submitMembershipRequest(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubmitMembershipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.submitMembershipRequest(user.getEmail(), request));
    }

    @GetMapping("/api/users/membership-requests")
    public ResponseEntity<List<UserMembershipRequestResponse>> getUserMembershipRequests(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(membershipService.getUserMembershipRequests(user.getEmail()));
    }

    @GetMapping("/api/admin/memberships")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AdminMembershipRecordResponse>> getAdminMemberships() {
        return ResponseEntity.ok(membershipService.getAdminMemberships());
    }

    @GetMapping("/api/admin/memberships/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminMembershipStatsResponse> getAdminMembershipStats() {
        return ResponseEntity.ok(membershipService.getAdminMembershipStats());
    }

    @PutMapping("/api/memberships/{id}")
    public ResponseEntity<MembershipResponse> updateMembership(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMembershipRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(membershipService.updateMembership(id, request, user));
    }

    @PutMapping("/api/memberships/{id}/cancel")
    public ResponseEntity<MembershipResponse> cancelMembership(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(membershipService.cancelMembership(id, user));
    }
}
