package com.example.fitnationweb.service;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MvcProgressService {

    private static final String PORTAL_PROGRESS_PATH = "/portal/progress";

    private final ProgressWebService progressWebService;

    public void populatePortalProgressModel(Long userId, Model model) {
        model.addAttribute("navSection", "progress");
        model.addAttribute("summary", progressWebService.getMySummary(userId));
        model.addAttribute("entries", progressWebService.getMyHistory(userId));
    }

    public MvcRedirect createPortalEntry(
            Long userId,
            String recordedAt,
            BigDecimal weight,
            BigDecimal bodyFatPercent,
            BigDecimal muscleMass,
            BigDecimal waistCm,
            BigDecimal chestCm,
            BigDecimal hipCm,
            String notes) {
        try {
            progressWebService.createMyEntry(userId, recordedAt, weight, bodyFatPercent, muscleMass, waistCm, chestCm, hipCm, notes);
            return MvcRedirect.to(PORTAL_PROGRESS_PATH, "Progress entry added.");
        } catch (Exception e) {
            return MvcRedirect.failure(PORTAL_PROGRESS_PATH, e.getMessage());
        }
    }

    public MvcRedirect updatePortalEntry(
            Long userId,
            Long entryId,
            String recordedAt,
            BigDecimal weight,
            BigDecimal bodyFatPercent,
            BigDecimal muscleMass,
            BigDecimal waistCm,
            BigDecimal chestCm,
            BigDecimal hipCm,
            String notes) {
        try {
            progressWebService.updateMyEntry(userId, entryId, recordedAt, weight, bodyFatPercent, muscleMass, waistCm, chestCm, hipCm, notes);
            return MvcRedirect.to(PORTAL_PROGRESS_PATH, "Progress entry updated.");
        } catch (Exception e) {
            return MvcRedirect.failure(PORTAL_PROGRESS_PATH, e.getMessage());
        }
    }

    public MvcRedirect deletePortalEntry(Long userId, Long entryId) {
        try {
            progressWebService.deleteMyEntry(userId, entryId);
            return MvcRedirect.to(PORTAL_PROGRESS_PATH, "Progress entry deleted.");
        } catch (Exception e) {
            return MvcRedirect.failure(PORTAL_PROGRESS_PATH, e.getMessage());
        }
    }

    public void populateAdminClientProgressModel(Long userId, Model model) {
        model.addAttribute("progressHistory", progressWebService.getClientHistoryForAdmin(userId));
        model.addAttribute("progressSummary", progressWebService.getClientSummaryForAdmin(null, UserRole.ADMIN, userId));
        model.addAttribute("userId", userId);
        model.addAttribute("navSection", "clients");
    }

    public void populateAdminClientProgressSummaryModel(Long userId, Model model) {
        model.addAttribute("progressSummary", progressWebService.getClientSummaryForAdmin(null, UserRole.ADMIN, userId));
        model.addAttribute("userId", userId);
        model.addAttribute("navSection", "clients");
    }

    public void populateClientProgressModel(Long actorUserId, UserRole actorRole, Long clientUserId, String navSection, Model model) {
        model.addAttribute("navSection", navSection);
        model.addAttribute("clientUserId", clientUserId);
        model.addAttribute("summary", progressWebService.getClientSummaryForAdmin(actorUserId, actorRole, clientUserId));
        if (actorRole == UserRole.TRAINER) {
            model.addAttribute("entries", progressWebService.getClientHistoryForTrainer(actorUserId, actorRole, clientUserId));
            return;
        }
        model.addAttribute("entries", progressWebService.getClientHistoryForAdmin(clientUserId));
    }
}

