package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.dto.response.AdminMemberStatsResponse;
import com.example.fitnationcommon.dto.response.MemberDetailResponse;
import com.example.fitnationcommon.dto.response.MemberListResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationuser.service.AdminMemberService;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class MvcAdminMemberService {

    private static final String MEMBERS_PATH = "/admin/members";

    private final AdminMemberService adminMemberService;
    private final MvcValidationService validationService;

    public AdminMemberStatsResponse stats() {
        return adminMemberService.getMemberStats();
    }

    public PagedResponse<MemberListResponse> list(Integer page, Integer size, String sort, String q, String status) {
        int p = validationService.normalizePage(page);
        int s = validationService.normalizeSize(size);
        return adminMemberService.getMembers(p, s, sort, q, status);
    }

    public MemberDetailResponse byId(Long id) {
        return adminMemberService.getMemberById(id);
    }

    public void populateStatsModel(Model model) {
        model.addAttribute("memberStats", stats());
        model.addAttribute("navSection", "members");
    }

    public void populateMembersModel(
            Integer page,
            Integer size,
            String sort,
            String q,
            String status,
            Model model) {
        model.addAttribute("members", list(page, size, sort, q, status));
        model.addAttribute("searchQuery", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("navSection", "members");
    }

    public void populateMemberDetailModel(Long id, Model model) {
        model.addAttribute("member", byId(id));
        model.addAttribute("navSection", "members");
    }

    public MvcRedirect create(CreateMemberRequest request) {
        try {
            adminMemberService.createMember(request);
            return MvcRedirect.to(MEMBERS_PATH, "Member created successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(MEMBERS_PATH, e.getMessage());
        }
    }

    public MvcRedirect invite(CreateMemberRequest request) {
        try {
            adminMemberService.inviteMember(request);
            return MvcRedirect.to(MEMBERS_PATH, "Member invited successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(MEMBERS_PATH, e.getMessage());
        }
    }

    public MvcRedirect update(Long id, UpdateMemberRequest request) {
        try {
            adminMemberService.updateMember(id, request);
            return MvcRedirect.to(MEMBERS_PATH, "Member updated successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(MEMBERS_PATH, e.getMessage());
        }
    }

    public MvcRedirect delete(Long id) {
        try {
            adminMemberService.deleteMember(id);
            return MvcRedirect.to(MEMBERS_PATH, "Member deleted successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(MEMBERS_PATH, e.getMessage());
        }
    }

}

