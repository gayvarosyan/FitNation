package com.example.fitnationweb.controller;

import com.example.fitnationweb.service.AdminUserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserMvcController {

    private final AdminUserManagementService adminUserManagementService;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("navSection", "users");
        return "admin/users";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        var result = adminUserManagementService.deleteUser(userId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/restore")
    public String restore(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        var result = adminUserManagementService.restoreDeletedUser(userId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
