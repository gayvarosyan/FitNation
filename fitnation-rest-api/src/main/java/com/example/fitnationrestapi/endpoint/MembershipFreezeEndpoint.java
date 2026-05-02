package com.example.fitnationrestapi.endpoint;

import com.example.fitnationcommon.dto.response.AdminFreezeRequestResponse;
import com.example.fitnationcommon.dto.request.RejectFreezeRequest;
import com.example.fitnationcommon.dto.request.RenewMembershipRequest;
import com.example.fitnationcommon.dto.request.SubmitFreezeRequest;
import com.example.fitnationcommon.dto.response.UserFreezeRequestResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.enums.FreezeRequestStatus;
import com.example.fitnationmembership.service.MembershipFreezeService;
import com.example.fitnationuser.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Membership freeze", description = "Freeze requests, renewal, and admin review")
public class MembershipFreezeEndpoint {

    private final MembershipFreezeService freezeService;

    @Operation(summary = "Submit freeze request")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Request created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/api/users/memberships/{membershipId}/freeze-requests")
    public ResponseEntity<UserFreezeRequestResponse> submitFreezeRequest(
            @AuthenticationPrincipal User user,
            @PathVariable Long membershipId,
            @Valid @RequestBody SubmitFreezeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(freezeService.submitFreezeRequest(user, membershipId, request));
    }

    @Operation(summary = "List freeze requests for membership")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Requests returned"))
    @GetMapping("/api/users/memberships/{membershipId}/freeze-requests")
    public ResponseEntity<List<UserFreezeRequestResponse>> getUserFreezeRequests(
            @AuthenticationPrincipal User user,
            @PathVariable Long membershipId) {
        return ResponseEntity.ok(freezeService.getUserFreezeRequests(user, membershipId));
    }

    @Operation(summary = "Renew membership", description = "Optional body for renewal parameters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership renewed"),
            @ApiResponse(responseCode = "400", description = "Cannot renew")
    })
    @PostMapping("/api/users/memberships/{membershipId}/renew")
    public ResponseEntity<MembershipResponse> renewMembership(
            @AuthenticationPrincipal User user,
            @PathVariable Long membershipId,
            @RequestBody(required = false) RenewMembershipRequest request) {
        return ResponseEntity.ok(freezeService.renewMembership(user, membershipId, request));
    }

    @Operation(summary = "List freeze requests (paged)", description = "ADMIN or CLIENT role per service rules.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Page returned"))
    @GetMapping("/api/admin/membership-freeze-requests")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Page<AdminFreezeRequestResponse>> listFreezeRequests(
            @RequestParam(required = false) FreezeRequestStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(freezeService.listFreezeRequests(status, pageable));
    }

    @Operation(summary = "Approve freeze request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request approved"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PostMapping("/api/admin/membership-freeze-requests/{requestId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<AdminFreezeRequestResponse> approveFreezeRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(freezeService.approveFreezeRequest(requestId, admin));
    }

    @Operation(summary = "Reject freeze request", description = "Optional rejection body.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request rejected"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PostMapping("/api/admin/membership-freeze-requests/{requestId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<AdminFreezeRequestResponse> rejectFreezeRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User admin,
            @RequestBody(required = false) RejectFreezeRequest body) {
        return ResponseEntity.ok(freezeService.rejectFreezeRequest(requestId, admin, body));
    }
}
