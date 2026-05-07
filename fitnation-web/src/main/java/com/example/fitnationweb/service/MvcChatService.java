package com.example.fitnationweb.service;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class MvcChatService {

    private static final String PAGE_PATH = "/portal/chat";

    private final ChatWebService chatWebService;

    public void populatePageModel(Long userId, Long conversationId, Model model) {
        model.addAttribute("navSection", "chat");
        model.addAttribute("conversations", chatWebService.listConversations(userId));
        model.addAttribute("selectedConversationId", conversationId);
        if (conversationId != null) {
            model.addAttribute("messages", chatWebService.getMessages(userId, conversationId));
        }
    }

    public MvcRedirect openConversation(Long userId, UserRole role, Long otherUserId) {
        try {
            var conv = chatWebService.openConversation(userId, role, otherUserId);
            return MvcRedirect.to(PAGE_PATH + "?conversationId=" + conv.id(), "Conversation opened.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE_PATH, e.getMessage());
        }
    }
}

