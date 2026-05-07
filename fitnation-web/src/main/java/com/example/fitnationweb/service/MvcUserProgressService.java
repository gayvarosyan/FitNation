package com.example.fitnationweb.service;

import com.example.fitnationprogress.dto.UpsertUserProgressEntryRequest;
import com.example.fitnationprogress.service.UserProgressService;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class MvcUserProgressService {

    private static final String PROGRESS_PATH = "/users/progress";

    private final ProgressWebService progressWebService;
    private final UserProgressService userProgressService;

    public void populateHistoryModel(Long userId, Model model) {
        model.addAttribute("progressHistory", progressWebService.getMyHistory(userId));
        model.addAttribute("progressSummary", progressWebService.getMySummary(userId));
        model.addAttribute("navSection", "progress");
    }

    public void populateSummaryModel(Long userId, Model model) {
        model.addAttribute("progressSummary", progressWebService.getMySummary(userId));
        model.addAttribute("navSection", "progress");
    }

    public Object getMyEntry(Long userId, Long entryId) {
        return userProgressService.getMyEntry(userId, entryId);
    }

    public void populateEntryModel(Long userId, Long entryId, Model model) {
        model.addAttribute("progressEntry", getMyEntry(userId, entryId));
        model.addAttribute("navSection", "progress");
    }

    public MvcRedirect createEntry(Long userId, UpsertUserProgressEntryRequest request) {
        try {
            userProgressService.createEntry(userId, request);
            return MvcRedirect.to(PROGRESS_PATH, "Progress entry created successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(PROGRESS_PATH, e.getMessage());
        }
    }

    public MvcRedirect updateEntry(Long userId, Long entryId, UpsertUserProgressEntryRequest request) {
        try {
            userProgressService.updateMyEntry(userId, entryId, request);
            return MvcRedirect.to(PROGRESS_PATH, "Progress entry updated successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(PROGRESS_PATH, e.getMessage());
        }
    }

    public MvcRedirect deleteEntry(Long userId, Long entryId) {
        try {
            userProgressService.deleteMyEntry(userId, entryId);
            return MvcRedirect.to(PROGRESS_PATH, "Progress entry deleted successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(PROGRESS_PATH, e.getMessage());
        }
    }
}

