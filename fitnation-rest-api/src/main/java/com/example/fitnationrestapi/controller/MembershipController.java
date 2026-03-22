package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationuser.membership.MembershipService;
import com.example.fitnationuser.membership.MembershipType;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<MembershipType>> getMembershipTypes() {
        return ResponseEntity.ok(membershipService.getAllMembershipTypes());
    }

    @PostMapping("/api/admin/membership-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipType> createMembershipType(
            @RequestBody CreateMembershipTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.createMembershipType(request));
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

    @PutMapping("/api/memberships/{id}/cancel")
    public ResponseEntity<MembershipResponse> cancelMembership(@PathVariable Long id) {
        return ResponseEntity.ok(membershipService.cancelMembership(id));
    }
}