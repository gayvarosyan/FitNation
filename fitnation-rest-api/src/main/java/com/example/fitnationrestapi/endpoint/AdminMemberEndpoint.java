package com.example.fitnationrestapi.endpoint;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.dto.response.AdminMemberStatsResponse;
import com.example.fitnationcommon.dto.response.MemberDetailResponse;
import com.example.fitnationcommon.dto.response.MemberListResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationuser.service.AdminMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin members", description = "Member directory and lifecycle (ADMIN)")
public class AdminMemberEndpoint {

    private final AdminMemberService adminMemberService;

    @Operation(summary = "Member statistics")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Stats returned"))
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminMemberStatsResponse> getMemberStats() {
        return ResponseEntity.ok(adminMemberService.getMemberStats());
    }

    @Operation(
            summary = "List members (paged)",
            description = """
            Searchable, paginated member directory.
            
            **q** searches: firstName, lastName, email (case-insensitive, partial match)
            **sort** allowed fields: createdAt, firstName, lastName, email, status
            **size** max: 100, default: 20
            **page** 0-based, default: 0
            **status** optional filter: ACTIVE, PENDING, BLOCKED
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination params (bad sort field, negative page, size > 100)")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<MemberListResponse>> getMembers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status) {

        return ResponseEntity.ok(adminMemberService.getMembers(page, size, sort, q, status));
    }

    @Operation(summary = "Get member by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member returned"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDetailResponse> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(adminMemberService.getMemberById(id));
    }

    @Operation(summary = "Create member")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDetailResponse> createMember(
            @Valid @RequestBody CreateMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminMemberService.createMember(request));
    }

    @Operation(summary = "Invite member")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Invitation sent / member created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDetailResponse> inviteMember(
            @Valid @RequestBody CreateMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminMemberService.inviteMember(request));
    }

    @Operation(summary = "Update member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member updated"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDetailResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(adminMemberService.updateMember(id, request));
    }

    @Operation(summary = "Delete member")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Member deleted"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        adminMemberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}