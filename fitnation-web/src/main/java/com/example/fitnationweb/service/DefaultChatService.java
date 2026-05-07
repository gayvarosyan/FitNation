package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.request.OpenConversationRequest;
import com.example.fitnationcommon.dto.response.ConversationResponse;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationweb.repository.ChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(ChatService.class)
public class DefaultChatService implements ChatService {

    private static final String CHAT_NOT_CONFIGURED =
            "Chat service backend is not configured for fitnation-web runtime.";

    @Override
    public ConversationResponse openOrGet(Long currentUserId, UserRole role, OpenConversationRequest req) {
        throw new IllegalStateException(CHAT_NOT_CONFIGURED);
    }

    @Override
    public Page<ConversationResponse> listConversations(Long userId, Pageable pageable) {
        return Page.empty(pageable);
    }

    @Override
    public Page<MessageResponse> getMessages(Long userId, Long conversationId, Pageable pageable) {
        return Page.empty(pageable);
    }
}

