package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.dto.response.MemberDetailResponse;
import com.example.fitnationcommon.dto.response.MemberListResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationweb.service.MvcClientAdminService;
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
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ClientMvcController {

    private static final String CLIENTS_PATH = "/admin/clients";

    private final MvcClientAdminService mvcClientAdminService;

    @GetMapping
    public String listClients(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            Model model) {
        
        var validationResult = mvcClientAdminService.validatePagination(page, size);
        if (validationResult.isInvalid()) {
            model.addAttribute("error", validationResult.message());
            return "admin/clients";
        }
        
        PagedResponse<MemberListResponse> members = mvcClientAdminService.list(page, size, sort, q, status);
        
        model.addAttribute("clients", members.getItems());
        model.addAttribute("stats", mvcClientAdminService.stats());
        model.addAttribute("searchQuery", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("navSection", "clients");
        return "admin/clients";
    }

    @GetMapping("/{id}")
    public String getClientById(@PathVariable Long id, Model model) {
        try {
            MemberDetailResponse client = mvcClientAdminService.byId(id);
            model.addAttribute("client", client);
            model.addAttribute("navSection", "clients");
            return "admin/client-detail";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:" + CLIENTS_PATH;
        }
    }

    @PostMapping("/create")
    public String createClient(@Valid CreateMemberRequest request, RedirectAttributes redirectAttributes) {
        var result = mvcClientAdminService.create(request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/invite")
    public String inviteClient(@Valid CreateMemberRequest request, RedirectAttributes redirectAttributes) {
        var result = mvcClientAdminService.invite(request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/{id}/update")
    public String updateClient(
            @PathVariable Long id,
            @Valid UpdateMemberRequest request,
            RedirectAttributes redirectAttributes) {
        var result = mvcClientAdminService.update(id, request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/{id}/delete")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = mvcClientAdminService.delete(id);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
