package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.dto.response.AdminMemberStatsResponse;
import com.example.fitnationcommon.dto.response.MemberDetailResponse;
import com.example.fitnationcommon.dto.response.MemberListResponse;
import com.example.fitnationuser.service.AdminMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
@Slf4j
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminMemberStatsResponse> getMemberStats() {

        return ResponseEntity.ok(adminMemberService.getMemberStats());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MemberListResponse>> getMembers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {

        return ResponseEntity.ok(adminMemberService.getMembers(page, size, search, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDetailResponse> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(adminMemberService.getMemberById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDetailResponse> createMember(
            @Valid @RequestBody CreateMemberRequest request) {

        MemberDetailResponse createdMember = adminMemberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @PostMapping("/invite")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MemberDetailResponse> inviteMember(
            @Valid @RequestBody CreateMemberRequest request) {

        MemberDetailResponse invited = adminMemberService.inviteMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(invited);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDetailResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request) {

        MemberDetailResponse updatedMember = adminMemberService.updateMember(id, request);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        adminMemberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
