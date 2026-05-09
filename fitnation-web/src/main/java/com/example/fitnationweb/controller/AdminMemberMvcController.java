package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationweb.service.MvcAdminMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMemberMvcController {

    private final MvcAdminMemberService mvcAdminMemberService;

    @GetMapping("/stats")
    public String getMemberStats(Model model) {
        try {
            mvcAdminMemberService.populateStatsModel(model);
            return "admin/member-stats";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/member-stats";
        }
    }

    @GetMapping
    public String getMembers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            Model model) {
        try {
            mvcAdminMemberService.populateMembersModel(page, size, sort, q, status, model);
            return "admin/members";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/members";
        }
    }

    @GetMapping("/{id}")
    public String getMemberById(@PathVariable Long id, Model model) {
        try {
            mvcAdminMemberService.populateMemberDetailModel(id, model);
            return "admin/member-detail";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/members";
        }
    }

    @PostMapping("/create")
    public String createMember(@Valid CreateMemberRequest request, RedirectAttributes redirectAttributes) {
        var result = mvcAdminMemberService.create(request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/invite")
    public String inviteMember(@Valid CreateMemberRequest request, RedirectAttributes redirectAttributes) {
        var result = mvcAdminMemberService.invite(request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/{id}/update")
    public String updateMember(
            @PathVariable Long id,
            @Valid UpdateMemberRequest request,
            RedirectAttributes redirectAttributes) {
        var result = mvcAdminMemberService.update(id, request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = mvcAdminMemberService.delete(id);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
