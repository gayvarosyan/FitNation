package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.dto.response.AdminMemberStatsResponse;
import com.example.fitnationcommon.dto.response.MemberListResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationuser.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientManagementService {

    private final AdminMemberService adminMemberService;

    public AdminMemberStatsResponse getStats() {
        return adminMemberService.getMemberStats();
    }

    public List<MemberListResponse> getDirectory() {
        PagedResponse<MemberListResponse> response = adminMemberService.getMembers(0, 100, "createdAt,desc", null, null);
        return response.getItems();
    }

    public void create(CreateMemberRequest request) {
        adminMemberService.createMember(request);
    }

    public void invite(CreateMemberRequest request) {
        adminMemberService.inviteMember(request);
    }

    public void edit(Long clientId, UpdateMemberRequest request) {
        adminMemberService.updateMember(clientId, request);
    }

    public void delete(Long clientId) {
        adminMemberService.deleteMember(clientId);
    }
}