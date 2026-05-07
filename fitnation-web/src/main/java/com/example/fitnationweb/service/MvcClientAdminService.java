package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.dto.response.MemberDetailResponse;
import com.example.fitnationcommon.dto.response.MemberListResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationuser.service.AdminMemberService;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MvcClientAdminService {

    private static final String CLIENTS_PATH = "/admin/clients";

    private final ClientManagementService clientManagementService;
    private final AdminMemberService adminMemberService;
    private final MvcValidationService validationService;

    public MvcValidationService.ValidationResult validatePagination(Integer page, Integer size) {
        return validationService.validatePagination(page, size);
    }

    public PagedResponse<MemberListResponse> list(Integer page, Integer size, String sort, String q, String status) {
        int p = validationService.normalizePage(page);
        int s = validationService.normalizeSize(size);
        return adminMemberService.getMembers(p, s, sort, q, status);
    }

    public Object stats() {
        return clientManagementService.getStats();
    }

    public MemberDetailResponse byId(Long id) {
        return adminMemberService.getMemberById(id);
    }

    public MvcRedirect create(CreateMemberRequest request) {
        try {
            clientManagementService.create(request);
            return MvcRedirect.to(CLIENTS_PATH, "Client created successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(CLIENTS_PATH, e.getMessage());
        }
    }

    public MvcRedirect invite(CreateMemberRequest request) {
        try {
            clientManagementService.invite(request);
            return MvcRedirect.to(CLIENTS_PATH, "Client invited successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(CLIENTS_PATH, e.getMessage());
        }
    }

    public MvcRedirect update(Long clientId, UpdateMemberRequest request) {
        try {
            clientManagementService.edit(clientId, request);
            return MvcRedirect.to(CLIENTS_PATH, "Client updated successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(CLIENTS_PATH, e.getMessage());
        }
    }

    public MvcRedirect delete(Long clientId) {
        try {
            clientManagementService.delete(clientId);
            return MvcRedirect.to(CLIENTS_PATH, "Client deleted successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(CLIENTS_PATH, e.getMessage());
        }
    }
}

