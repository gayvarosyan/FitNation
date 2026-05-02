package com.example.fitnationrestapi.endpoint;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PageRequestParams;
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
import com.example.fitnationcommon.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.util.Set;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Memberships", description = "Membership types, purchases, requests, and admin membership management")
public class MembershipEndpoint {

    private final MembershipService membershipService;

    @Operation(summary = "List membership types", description = "Catalog of available membership plans (authenticated).")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Membership types returned"))
    @GetMapping("/api/membership-types")
    public ResponseEntity<List<MembershipTypeResponse>> getMembershipTypes() {
        return ResponseEntity.ok(membershipService.getAllMembershipTypes());
    }

    @Operation(summary = "Create membership type", description = "ADMIN: add a plan type.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Type created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/api/admin/membership-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipTypeResponse> createMembershipType(
            @RequestBody CreateMembershipTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.createMembershipType(request));
    }

    @Operation(summary = "Update membership type", description = "ADMIN: update a plan type.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Type updated"),
            @ApiResponse(responseCode = "404", description = "Type not found")
    })
    @PutMapping("/api/admin/membership-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipTypeResponse> updateMembershipType(
            @PathVariable Long id,
            @RequestBody CreateMembershipTypeRequest request) {
        return ResponseEntity.ok(membershipService.updateMembershipType(id, request));
    }

    @Operation(summary = "Delete membership type", description = "ADMIN: remove a plan type.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Type deleted"),
            @ApiResponse(responseCode = "404", description = "Type not found")
    })
    @DeleteMapping("/api/admin/membership-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMembershipType(@PathVariable Long id) {
        membershipService.deleteMembershipType(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Purchase membership", description = "Creates an active membership for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Membership created"),
            @ApiResponse(responseCode = "400", description = "Invalid request or business rule violation")
    })
    @PostMapping("/api/users/memberships")
    public ResponseEntity<MembershipResponse> purchaseMembership(
            @AuthenticationPrincipal User user,
            @RequestBody PurchaseMembershipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.purchaseMembership(user.getEmail(), request));
    }

    @Operation(
            summary = "List current user's memberships (paged)",
            description = """
                Paginated membership list for the authenticated user.

                **sort** allowed fields: createdAt, status, startDate, endDate  
                **size** max: 100, default: 20  
                **page** 0-based, default: 0  
                **status** optional filter: ACTIVE, EXPIRED, CANCELLED, PAST_DUE  

                **q** — not supported for this endpoint (ignored).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination params (bad sort field, negative page, size > 100)")
    })
    @GetMapping("/api/users/memberships")
    public ResponseEntity<PagedResponse<MembershipResponse>> getUserMemberships(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequestParams.toPageable(
                page, size, sort,
                Set.of("createdAt", "status", "startDate", "endDate")
        );

        return ResponseEntity.ok(
                PagedResponse.of(membershipService.getUserMemberships(user.getEmail(), pageable), sort)
        );
    }

    @Operation(summary = "Submit membership request", description = "User requests a new membership plan change.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Request submitted"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/api/users/membership-requests")
    public ResponseEntity<UserMembershipRequestResponse> submitMembershipRequest(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubmitMembershipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.submitMembershipRequest(user.getEmail(), request));
    }

    @Operation(summary = "List my membership requests")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Requests returned"))
    @GetMapping("/api/users/membership-requests")
    public ResponseEntity<List<UserMembershipRequestResponse>> getUserMembershipRequests(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(membershipService.getUserMembershipRequests(user.getEmail()));
    }

    @Operation(
            summary = "List memberships (admin view, paged)",
            description = """
                Searchable, paginated membership records for admin.

                **q** searches: user firstName, lastName, email, membership type name (case-insensitive, partial match)  
                **sort** allowed fields: createdAt, status, startDate, endDate, memberName  
                **size** max: 100, default: 20  
                **page** 0-based, default: 0  
                **status** optional filter: ACTIVE, EXPIRED, CANCELLED, PAST_DUE
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination params (bad sort field, negative page, size > 100)")
    })
    @GetMapping("/api/admin/memberships")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<AdminMembershipRecordResponse>> getAdminMemberships(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequestParams.toPageable(
                page, size, sort,
                Set.of("createdAt", "status", "startDate", "endDate", "memberName")
        );

        return ResponseEntity.ok(
                PagedResponse.of(membershipService.getAdminMemberships(pageable, q, status), sort)
        );
    }

    @Operation(summary = "Membership statistics (admin)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Stats returned"))
    @GetMapping("/api/admin/memberships/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminMembershipStatsResponse> getAdminMembershipStats() {
        return ResponseEntity.ok(membershipService.getAdminMembershipStats());
    }

    @Operation(summary = "Update membership", description = "Owner may update allowed fields on their membership.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership updated"),
            @ApiResponse(responseCode = "400", description = "Invalid update"),
            @ApiResponse(responseCode = "404", description = "Membership not found")
    })
    @PutMapping("/api/memberships/{id}")
    public ResponseEntity<MembershipResponse> updateMembership(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMembershipRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(membershipService.updateMembership(id, request, user));
    }

    @Operation(summary = "Cancel membership")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership cancelled"),
            @ApiResponse(responseCode = "404", description = "Membership not found")
    })
    @PutMapping("/api/memberships/{id}/cancel")
    public ResponseEntity<MembershipResponse> cancelMembership(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(membershipService.cancelMembership(id, user));
    }
}
