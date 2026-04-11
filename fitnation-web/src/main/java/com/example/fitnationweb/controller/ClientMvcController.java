package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationweb.service.ClientManagementService;
import com.example.fitnationweb.support.MvcRedirect;
import jakarta.validation.Valid;
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
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ClientMvcController {

    private static final String PAGE = "/admin/clients";

    private final ClientManagementService clientManagementService;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("stats", clientManagementService.getStats());
        model.addAttribute("clients", clientManagementService.getDirectory());
        model.addAttribute("navSection", "clients");
        return "admin/clients";
    }

    @PostMapping("/create")
    public String create(@Valid CreateMemberRequest request, RedirectAttributes redirectAttributes) {
        var result = createClient(request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/invite")
    public String invite(@Valid CreateMemberRequest request, RedirectAttributes redirectAttributes) {
        var result = inviteClient(request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/edit")
    public String edit(
            @RequestParam Long clientId,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String password,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam UserStatus status,
            @RequestParam(required = false) Long assignedTrainerId,
            @RequestParam(required = false) Long assignedNutritionPlanId,
            RedirectAttributes redirectAttributes) {
        var result = updateClient(clientId, firstName, lastName, password, phone, email, status, assignedTrainerId, assignedNutritionPlanId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long clientId, RedirectAttributes redirectAttributes) {
        var result = deleteClient(clientId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    private MvcRedirect createClient(CreateMemberRequest request) {
        try {
            clientManagementService.invite(request);
            return MvcRedirect.to(PAGE, "Client invited and email sent.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE, e.getMessage());
        }
    }

    private MvcRedirect inviteClient(CreateMemberRequest request) {
        try {
            clientManagementService.invite(request);
            return MvcRedirect.to(PAGE, "Client invited and email sent.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE, e.getMessage());
        }
    }

    private MvcRedirect updateClient(
            Long clientId,
            String firstName,
            String lastName,
            String password,
            String phone,
            String email,
            UserStatus status,
            Long assignedTrainerId,
            Long assignedNutritionPlanId) {
        try {
            String pwd = password != null && !password.isBlank() ? password : null;
            UpdateMemberRequest request = new UpdateMemberRequest();
            request.setFirstName(firstName.trim());
            request.setLastName(lastName.trim());
            request.setEmail(email != null && !email.isBlank() ? email.trim() : null);
            request.setPhone(phone.trim());
            request.setPassword(pwd);
            request.setAssignedTrainerId(assignedTrainerId);
            request.setAssignedNutritionPlanId(assignedNutritionPlanId);
            clientManagementService.edit(clientId, request);
            return MvcRedirect.to(PAGE, "Client updated.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE, e.getMessage());
        }
    }

    private MvcRedirect deleteClient(Long clientId) {
        try {
            clientManagementService.delete(clientId);
            return MvcRedirect.to(PAGE, "Client deleted.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE, e.getMessage());
        }
    }
}
