package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.request.OpenConversationRequest;
import com.example.fitnationcommon.dto.response.ConversationResponse;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationweb.repository.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatWebService {

    private static final int CONVERSATION_PAGE_SIZE = 100;
    private static final int MESSAGE_PAGE_SIZE = 200;

    private final ChatService chatService;

    public ConversationResponse openConversation(Long currentUserId, UserRole role, Long otherUserId) {
        return chatService.openOrGet(currentUserId, role, toOpenConversationRequest(role, otherUserId));
    }

    public List<ConversationResponse> listConversations(Long userId) {
        return chatService
                .listConversations(
                        userId,
                        PageRequest.of(0, CONVERSATION_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "lastMessageAt")))
                .getContent();
    }

    public List<MessageResponse> getMessages(Long userId, Long conversationId) {
        return chatService
                .getMessages(
                        userId,
                        conversationId,
                        PageRequest.of(0, MESSAGE_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();
    }

    private static OpenConversationRequest toOpenConversationRequest(UserRole role, Long otherUserId) {
        return role == UserRole.CLIENT
                ? new OpenConversationRequest(otherUserId, null)
                : new OpenConversationRequest(null, otherUserId);
    }
}
