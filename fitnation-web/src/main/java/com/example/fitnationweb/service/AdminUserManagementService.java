package com.example.fitnationweb.service;

import com.example.fitnationuser.service.UserAdminService;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserManagementService {

    private static final String PAGE_PATH = "/admin/users";

    private final UserAdminService userAdminService;

    public void softDeleteUser(Long id) {
        userAdminService.softDeleteUser(id);
    }

    public void restoreUser(Long id) {
        userAdminService.restoreUser(id);
    }

    public MvcRedirect deleteUser(Long userId) {
        try {
            softDeleteUser(userId);
            return MvcRedirect.to(PAGE_PATH, "User soft-deleted.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE_PATH, e.getMessage());
        }
    }

    public MvcRedirect restoreDeletedUser(Long userId) {
        try {
            restoreUser(userId);
            return MvcRedirect.to(PAGE_PATH, "User restored.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE_PATH, e.getMessage());
        }
    }
}
