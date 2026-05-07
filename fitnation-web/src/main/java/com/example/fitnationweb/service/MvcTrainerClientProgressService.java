package com.example.fitnationweb.service;

import com.example.fitnationweb.support.CurrentUserAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class MvcTrainerClientProgressService {

    private final ProgressWebService progressWebService;
    private final CurrentUserAccessor currentUserAccessor;

    public void populateHistoryModel(Long clientUserId, Model model) {
        var me = currentUserAccessor.requireUser();
        model.addAttribute("progressHistory", progressWebService.getClientHistoryForTrainer(me.getId(), me.getRole(), clientUserId));
        model.addAttribute("progressSummary", progressWebService.getClientSummaryForAdmin(me.getId(), me.getRole(), clientUserId));
        model.addAttribute("userId", clientUserId);
        model.addAttribute("navSection", "clients");
    }

    public void populateSummaryModel(Long clientUserId, Model model) {
        var me = currentUserAccessor.requireUser();
        model.addAttribute("progressSummary", progressWebService.getClientSummaryForAdmin(me.getId(), me.getRole(), clientUserId));
        model.addAttribute("userId", clientUserId);
        model.addAttribute("navSection", "clients");
    }
}

